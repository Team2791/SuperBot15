package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

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
	
	// auto lift related varaibles
	private boolean autoLift = false;
	private boolean toteReadyToPickup = false;
	private Timer liftAtTargetTimer = new Timer();
	private int botToteIndex = -1, stackHeight = 3;
		
	public Elevator(){
		lift = new Talon(Electronics.ELEVATOR_TALON);
		
		topSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_TOP);
		botSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_BOT);
		
		encoder = new Encoder(Electronics.ELEVATOR_ENC_A, Electronics.ELEVATOR_ENC_B, false, CounterBase.EncodingType.k4X);
		encoder.setDistancePerPulse(Constants.ELEVATOR_DIST_PER_PULSE);
		encoder.stopLiveWindowMode();
		
		piston = new DoubleSolenoid(Electronics.ELEVATOR_PISTON_FOR, Electronics.ELEVATOR_PISTON_REV);
		
		liftAtTargetTimer.reset();
	}
	
	// public get methods
	public double getHeight() 	{ return encoder.getDistance(); }
	public double getPresetIndex() 	{ return currentPresetIndex; }
	public double getPresetValue(int index)  { return Constants.ELEVATOR_PRESETS[index]; }
	public double getTargetHeight()	{ return targetHeight; }
	public double getOutput() { return output; }
	public boolean atTarget() { 
		return Math.abs(getHeight() - getTargetHeight()) < Constants.ELEVATOR_AT_TARGET_ERROR_THRESHOLD;
	}
	
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
			goToPreset(currentPresetIndex + 1);
	}
	public void decreasePreset() {
		if(currentPresetIndex > 0)
			goToPreset(currentPresetIndex-1);
	}
	
	public void goToPreset(int index) {
		manualControl = false;
		System.out.println("going from preset "+currentPresetIndex + " to " + index);
		// check if the preset was zero and is increasing assume we picked up a tote
		if(currentPresetIndex == 0 && index > 0)
			holdingTote = true;
		
		currentPresetIndex = index;
		targetHeight = getPresetValue(index);
	}
	
	public void setAutoLift(boolean enabled) { 
		if(enabled) {
			manualControl = false;
		}
		autoLift = enabled;
	}
	
	// private set methods
	private void setOutput(double output) {
		// TODO: check that it's okay to set the output + or -
		this.output = output;
		lift.set(output);
	}
		
	/**
	 * Old method to extend the tote piston. Replaced by extendTotePiston()
	 * @deprecated
	 */
	public void extend(){ piston.set(Value.kForward); }
	/**
	 * Old method to retract the tote piston. Replaced by retractTotePiston()
	 * @deprecated
	 */
	public void retract(){ piston.set(Value.kReverse); }
	
	public void extendTotePiston(){ piston.set(Value.kForward); }
	public void retractTotePiston(){ piston.set(Value.kReverse); }
	
	public void run() {
		checkEncoderCalibration();
		if(!manualControl) {
			
			// based on where we are and what we want figure out what to do
			// check if need to go up or down
			double heightDiff = targetHeight - getHeight();
			double holdingHeightDiff = dropingToteHeight - getHeight();
			
			if(heightDiff < 0.05 && heightDiff > -0.1 && currentPresetIndex != 0) {
				// if close don't move (much) or change tote piston
				setOutput(0.08);
				
			} else if(heightDiff > 0) {
				// we need to go up
				// check if we should be going up fast or slow
				// we want fast if we're far from the target slow if we're close
				if(heightDiff > Constants.ELEVATOR_SPEED_THRESHOLD) {
					// the height difference is large, we want to get there fast
					setOutput(0.7);
				} else {
					// the height difference is small which means we're closing in on the hooks
					// don't extend for the bottom
					if(currentPresetIndex == 0 || currentPresetIndex == 1) {
						retractTotePiston();
					} else {
						extendTotePiston();
					}	
					
					setOutput(0.25);
					// record this tote height so the drop code knows how long to go slow
					dropingToteHeight = targetHeight;
					dropingTotePresetIndex = currentPresetIndex;
				}
			} else {
				// need to go down
				// if dropping off tote check if still close to hooks
				if(holdingTote) {
					// this starts at 0 and gets larger as the elevator does down
					if(holdingHeightDiff < Constants.ELEVATOR_SPEED_THRESHOLD) {
						// still close to where the tote is being dropped off
						// only extend when going to not bottom or first preset
						if(currentPresetIndex != 0 && currentPresetIndex != 1)
							extendTotePiston();
						setOutput(-0.15);
					} else {
						// the height difference is large, record the tote is dropped off
						holdingTote = false;
						retractTotePiston();
						// stay slow, will go fast in the else case
						setOutput(-0.15);
					}
				} else {
					// not holding tote go down fast 
					// height diff starts negative and gets closer to 0
					if(heightDiff < -Constants.ELEVATOR_SPEED_THRESHOLD) {
						// the height difference is large, we want to get there fast
						setOutput(-0.8);
						// retarct the tote piston if going to the bottom preset
						// this might be the result of a bug, not enough time to find it
						if(currentPresetIndex == 0) {
							retractTotePiston();
						}
					} else {
						// the height difference is small which means we're closing in on the target
//						extendTotePiston();
						setOutput(-0.10);
					}
				}
			}
		SmartDashboard.putNumber("heightDiff", heightDiff);
		SmartDashboard.putNumber("holdingHeightDiff", holdingHeightDiff);
		} else { // if manual contrl
			targetHeight = getHeight();
			setOutputManual(Robot.operator.getAxis(Constants.AXIS_LS_Y));
		}
		
		// otuptu
		SmartDashboard.putNumber("targetHeight", targetHeight);
		SmartDashboard.putNumber("dropingToteHeight", dropingToteHeight);
		SmartDashboard.putBoolean("holdingTote", holdingTote);
	}
	
	public void disable(){
		// check for someone manually pushing the elevator down
		checkEncoderCalibration();
		lift.set(0.0);
	}
	
	public boolean getToteReadyToPickup() {
		// if we add limit switches this will return more than a varaible
		return toteReadyToPickup;
	}
	
	public void setToteReadyToPickup(boolean value) {
		// this is to set the varaible tote at bottom 
		toteReadyToPickup = value;
	}
	
	public void runAutoLift() {
		if(autoLift) {
			// check if the tote is at the bottom
			if(getToteReadyToPickup() && atBot()) {
				// chose the preset to send it to and record it
				// if there are no totes on the elevator
				if(botToteIndex == -1) {
					int target = stackHeight;
					goToPreset(target);
					botToteIndex = target;
				} else {
					// deincrememnt botToteIndex then tell goToPreset to go there
					goToPreset(--botToteIndex);
				}
				// reset tote ready to pickup. Take this out when we have sensors
				setToteReadyToPickup(false);
				// reset the tote 
				
				// don't bother checking other if statments
				return;
			} // end if(getToteReadyToPickup() && atBot()) {
			
			// check if the tote is at the top and we should start counting
			if(atTarget() && liftAtTargetTimer.get() > Constants.ELEVATOR_AUTO_LIFT_SETTLE_TIME) {
				// tell the elevator to back to the bottom
				goToPreset(0);
			} 
		}
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
		
		// reset auto lift
		autoLift = false;
		toteReadyToPickup = false;
		botToteIndex = -1;
		stackHeight = 3;
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
