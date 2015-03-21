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
	public boolean[] elevatorPositionTrack = {false, false, false, false, false, false};
	
	// auto lift related varaibles
	public boolean autoLift = false;
	private boolean toteReadyToPickup = false;
	private Timer liftAtTargetTimer = new Timer();
	public int botToteIndex = -1;
	private int stackHeight = 3;
		
	public Elevator(){
		lift = new Talon(Electronics.ELEVATOR_TALON);
		
		topSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_TOP);
		botSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_BOT);
		
		encoder = new Encoder(Electronics.ELEVATOR_ENC_A, Electronics.ELEVATOR_ENC_B, false, CounterBase.EncodingType.k4X);
		encoder.setDistancePerPulse(Constants.ELEVATOR_DIST_PER_PULSE);
		encoder.stopLiveWindowMode();
		
		piston = new DoubleSolenoid(Electronics.ELEVATOR_PISTON_FOR, Electronics.ELEVATOR_PISTON_REV);
		
		liftAtTargetTimer.start();
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
		
		// run the tote piston
		if(getHeight() > Constants.ELEVATOR_PISTON_EXTEND_THRESHOLD) {
			extendTotePiston();
		} else {
			retractTotePiston();
		}
		
		if(!manualControl) {
			
			// based on where we are and what we want figure out what to do
			// check if need to go up or down
			double heightDiff = targetHeight - getHeight();
			double holdingHeightDiff = dropingToteHeight - getHeight();
			
			// run the at target timer
			if(!atTarget())
				liftAtTargetTimer.reset();
			
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
						setOutput(-0.15);
					} else {
						// the height difference is large, record the tote is dropped off
						holdingTote = false;
						// stay slow, will go fast in the else case
						setOutput(-0.15);
					}
				} else {
					// not holding tote go down fast 
					// height diff starts negative and gets closer to 0
					if(heightDiff < -Constants.ELEVATOR_SPEED_THRESHOLD) {
						// the height difference is large, we want to get there fast
						setOutput(-0.8);
					} else {
						// the height difference is small which means we're closing in on the target
//						extendTotePiston();
						setOutput(-0.10);
					}
				}
			}
			SmartDashboard.putNumber("heightDiff", heightDiff);
			SmartDashboard.putNumber("holdingHeightDiff", holdingHeightDiff);
			SmartDashboard.putBoolean("READY TO DROP", (atTarget() && liftAtTargetTimer.get() > Constants.ELEVATOR_AUTO_LIFT_SETTLE_TIME));
		} else if (!autoLift){
			targetHeight = getHeight();
			setOutputManual(-Robot.operator.getAxis(Constants.AXIS_LS_Y));
		}
		
		
		// otuptu
		SmartDashboard.putNumber("targetHeight", targetHeight);
		SmartDashboard.putNumber("dropingToteHeight", dropingToteHeight);
		SmartDashboard.putBoolean("holdingTote", holdingTote);
		
		
		
		for(int x = 0; x < 6; x++){
			elevatorPositionTrack[x] = false;
		}
		elevatorPositionTrack[currentPresetIndex] = true;

		SmartDashboard.putBoolean("Ground", elevatorPositionTrack[0]);
		SmartDashboard.putBoolean("Hook4", elevatorPositionTrack[5]);
		SmartDashboard.putBoolean("Hook3", elevatorPositionTrack[4]);
		SmartDashboard.putBoolean("Hook2", elevatorPositionTrack[3]);
		SmartDashboard.putBoolean("Hook1", elevatorPositionTrack[2]);
		SmartDashboard.putBoolean("BotPos", elevatorPositionTrack[1]);
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
		if(value){
			autoLift = true;
			System.out.println("autolift set true");
		}
	}
	
	public void runAutoLift() {
		if(autoLift){
			System.out.println("autolift is true, executing runautolift");
			// check if the tote is at the bottom
			if(getToteReadyToPickup() && atBot()) {
				// chose the preset to send it to and record it
				// if there are no totes on the elevator
				if(botToteIndex == -1) {
					int target = Constants.ELEVATOR_STACK_HEIGHT;
					goToPreset(target);
					botToteIndex = target;
				} else {
					// decrememnt botToteIndex then tell goToPreset to go there
					goToPreset(--botToteIndex);
					System.out.println("decreasing bot tote index");
				}
				// reset tote ready to pickup. Take this out when we have sensors
				setToteReadyToPickup(false);
				
				// don't bother checking other if statments
				return;
			// if not grabbing a tote check if it's time to drop one off
			} else if(atTarget() && liftAtTargetTimer.get() > Constants.ELEVATOR_AUTO_LIFT_SETTLE_TIME) {
				// tell the elevator to back to the bottom
				System.out.println("at target, going down");
				if(botToteIndex != 1){
					System.out.println("going to preset 0, bottoteindex is not 1");
					goToPreset(0);
				}
				autoLift = false;
				System.out.println("autolift set false");
			}
		}
		
		SmartDashboard.putNumber("botToteIndex", botToteIndex);
		SmartDashboard.putNumber("Going to hook:", botToteIndex-1);
		SmartDashboard.putBoolean("ToteReadyToPickup", getToteReadyToPickup());
		SmartDashboard.putNumber("stackHeight", stackHeight);
		SmartDashboard.putNumber("liftAtTargetTimer", liftAtTargetTimer.get());
	}
	
	public void autoLiftReset() {
		botToteIndex = -1;
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
			return "Manual/AutoLift";
		else
			return "Automatic";
	}
	
	public boolean getEncoderCalibrated() {
		return encoderCalibrated;
	}
}
