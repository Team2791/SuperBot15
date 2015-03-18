package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;

public class Elevator{
	// components 
	private static Talon lift;
	private static Encoder encoder;
	private static DigitalInput topSwitch;
	private static DigitalInput botSwitch;
	private static DoubleSolenoid piston;
	
	// state variables 
	public boolean manualControl = false;
	private boolean encoderCalibrated = false;
	private boolean holdingTote       = false;
	private double  targetHeight       = 0;
	private int     currentPresetIndex = 0;
	private double	dropingToteHeight  = 0;
	private int 	dropingTotePresetIndex  = 0;
	private double  output = 0;
		
	public Elevator(){
		lift = new Talon(Electronics.ELEVATOR_TALON);
		
		topSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_TOP);
		botSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_BOT);
		
		encoder = new Encoder(Electronics.ELEVATOR_ENC_A, Electronics.ELEVATOR_ENC_B, false, CounterBase.EncodingType.k4X);
		encoder.setDistancePerPulse(Constants.ELEVATOR_DIST_PER_PULSE);
		encoder.stopLiveWindowMode();
		
		piston = new DoubleSolenoid(Electronics.ELEVATOR_PISTON_FOR, Electronics.ELEVATOR_PISTON_REV);

	}
	
	// public get methods
	public double getHeight() 	{ return encoder.getDistance(); }
	public double getPresetIndex() 	{ return currentPresetIndex; }
	public double getPresetValue(int index)  { return Constants.ELEVATOR_PRESETS[index]; }
	public double getTargetHeight()	{ return targetHeight; }
	public double getOutput() { return output; }
	
	// public set methods
	/**
	 * Set the output of the elevator and stop any automatic routine the elevator is doing
	 * @param output
	 */
	public void setOutputManual(double output) {
		manualControl = true;
		setOutput(output);	
	}
	
	// increase and decrease preset if it's valid to do so
	public void increasePreset() {
		if(currentPresetIndex < Constants.ELEVATOR_PRESETS.length - 1)
			currentPresetIndex++;
		goToPreset(currentPresetIndex);
	}
	public void decreasePreset() {
		if(currentPresetIndex > 0)
			currentPresetIndex--;
		goToPreset(currentPresetIndex);
	}
	
	public void goToPreset(int index) {
		manualControl = false;
		// check if the preset was zero and is increasing assume we picked up a tote
		if(currentPresetIndex == 0 && index > 0)
			holdingTote = true;
		
		currentPresetIndex = index;
		targetHeight = getPresetValue(index);
	}
	
	// private set methods
	private void setOutput(double output) {
		// TODO: check that it's okay to set the output + or -
		this.output = output;
		lift.set(output);
	}

	public void extend(){ piston.set(Value.kForward); }
	public void retract(){ piston.set(Value.kReverse); }
	
	public void run() {
		checkEncoderCalibration();
		if(!manualControl) {
			// based on where we are and what we want figure out what to do
			// check if need to go up or down
			double heightDiff = targetHeight - getHeight();
			if(heightDiff > 0) {
				// we need to go up
				// check if we should be going up fast or slow
				// we want fast if we're far from the target slow if we're close
				if(heightDiff > Constants.ELEVATOR_SPEED_THRESHOLD) {
					// the height difference is large, we want to get there fast
					setOutput(0.7);
				} else {
					// the height difference is small which means we're closing in on the hooks
					extend();
					setOutput(0.23);
					// record this tote height so the drop code knows how long to go slow
					dropingToteHeight = targetHeight;
					dropingTotePresetIndex = currentPresetIndex;
				}
			} else {
				// need to go down
				// if dropping off tote check if still close to hooks
				if(holdingTote) {
					double holdingHeightDiff = dropingToteHeight - getHeight();
					if(holdingHeightDiff < -Constants.ELEVATOR_SPEED_THRESHOLD) {
						// the height difference is large, the tote is dropped off
						holdingTote = false;
						retract();
						setOutput(-0.5);
					} else {
						// we're still letting the tote down gently
						setOutput(-.15);
					}
					
				} else {
					if(heightDiff < -Constants.ELEVATOR_SPEED_THRESHOLD) {
						// the height difference is large, we want to get there fast
						setOutput(-0.5);
					} else {
						// the height difference is small which means we're closing in on the target
						setOutput(-.15);
					}
				}	
			}
		}
		else{
			targetHeight = getHeight();
			setOutput(Robot.operator.getAxis(Constants.AXIS_LS_Y));
		}
	}
	
	public void disable(){
		// check for someone manually pushing the elevator down
		checkEncoderCalibration();
		lift.set(0.0);
	}
	
	/**
	 * Reset elevator state variables. 
	 */
	public void reset() {
		holdingTote       = false;
		targetHeight       = 0;
		currentPresetIndex = 0;
		dropingToteHeight  = 0;
		dropingTotePresetIndex  = 0;
		manualControl = true;
	}
	
	public void resetEncoder()  { encoder.reset(); }
	
	public void checkEncoderCalibration() {
		if(botSwitch.get()) {
			encoder.reset();
			encoderCalibrated = true;
		}
	}
	
	public boolean atTop() {
		if(encoderCalibrated) {
			return topSwitch.get() || encoder.getDistance() > Constants.ELEVATOR_MAX_HEIGHT - Constants.ELEVATOR_STOP_ZONE;
		} else {
			return topSwitch.get();
		}
			
	}
	public boolean atBot() {
		if(encoderCalibrated) {
			return botSwitch.get();// || encoder.get() < Constants.ELEVATOR_STOP_ZONE;
		} else {
			return botSwitch.get();
		}
	}
	
	public String getPistonState(){
		if(piston.get().equals(Value.kForward))
			return "Extended";
		else if(piston.get().equals(Value.kReverse))
			return "Retracted";
		else
			return "Unknown";
	}
	
	public String getControlState(){
		if(manualControl)
			return "Manual";
		else
			return "Automatic";
	}
	
	public boolean getEncoderCalibrated() {
		return encoderCalibrated;
	}
}
