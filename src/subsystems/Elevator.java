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
	public  static Talon lift;
	public         ElevatorPID elevatorPID;
	private static double PID_P;
	private static double PID_I;
	private static double PID_D;
	public         Encoder encoder;
	public  static DigitalInput topSwitch;
	public  static DigitalInput botSwitch;
	public  static DoubleSolenoid piston;
	
	// state variables 
	public boolean  encoderCalibrated  = false;
	public boolean  droppingTote       = false;
	public double	droppedToteHeight  = 0;
	public int 		droppedTotePreset  = 0;
	public double   currentPreset      = 0;
	public int      currentPresetIndex = 0;
	public double   lastPreset         = 0;
	public int      lastPresetIndex    = 0;
	private boolean triggeredInc       = false;
	private boolean triggeredDec       = false;
	private boolean triggeredStart     = false;
	private boolean reachedHooks[]     = {true, false, false, false, false, false};
	private Timer   caseTimer;
	private static boolean manualControl;
	private static boolean triggeredSwap;
	
	
	public Elevator() {
		lift = new Talon(Electronics.ELEVATOR_TALON);
		
		caseTimer = new Timer();
		
		topSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_TOP);
		botSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_BOT);
		
		encoder = new Encoder(Electronics.ELEVATOR_ENC_A, Electronics.ELEVATOR_ENC_B, false, CounterBase.EncodingType.k4X);
		encoder.setDistancePerPulse(Constants.ELEVATOR_DIST_PER_PULSE);
		encoder.stopLiveWindowMode();
		
		piston = new DoubleSolenoid(Electronics.ELEVATOR_PISTON_FOR, Electronics.ELEVATOR_PISTON_REV);
		

		PID_P = Robot.dash.getDoubleFix("Ele_P", 3.00);
		PID_I = Robot.dash.getDoubleFix("Ele_I", 0.50);
		PID_D = Robot.dash.getDoubleFix("Ele_D", 0.00);
		
		elevatorPID = new ElevatorPID(PID_P, PID_I, PID_D);
		elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT);
		elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT);
		
		manualControl = true;
		triggeredSwap = false;
	}
	
//	public void testRun(){
//		// check for encoder calibration
//		if(botSwitch.get()) {
//			encoder.reset();
//			encoderCalibrated = true;
//		}
//				
//		double output = -Robot.operator.getAxis(Constants.AXIS_LS_Y) * Constants.ELEVATOR_SCALE;
//		
//		if(atBot() && -output > 0.0)
//			output = 0.0;
//		else if(atTop() && -output < 0.0)
//			output = 0.0;
//		
//		lift.set(output);
//		
//		//if(encoder.getDistance() > elevatorPID.getPresets()[1] * Constants.ELEVATOR_PISTON_HEIGHT_SCALE)
//		//	piston.set(Value.kForward);
//		//else
//		//	piston.set(Value.kReverse);
//		
////		if(Robot.operator.getRawButton(Constants.BUTTON_SEL)){
////			if(getPistonState().equals("Unknown"))
////				retract();
////			else if(getPistonState().equals("Extended"))
////				retract();
////			else
////				extend();
////		}
//		
//		if(Robot.operator.getRawButton(Constants.BUTTON_LB))
//			this.retract();
//		if(Robot.operator.getRawButton(Constants.BUTTON_RB))
//			this.extend();
//		
//		if(Robot.operator.getRawButton(Constants.BUTTON_SEL))
//			encoder.reset();
//	}
	
//	public void init(){
//		this.setOutputManual(0.15);
//		while(encoder.getDistance() <= 69.4){
//			Robot.dash.debug();
//			continue;
//		}
//		setOutputManual(0.0);
//	}
	
	public void init(){
		
		PID_P = Robot.dash.getDoubleFix("Ele_P", 3.00);
		PID_I = Robot.dash.getDoubleFix("Ele_I", 0.50);
		PID_D = Robot.dash.getDoubleFix("Ele_D", 0.00);
		
		elevatorPID.update_values(PID_P, PID_I, PID_D);
	}
	
	public void disabledPeriodic(){
		if(botSwitch.get()) {
			encoder.reset();
			encoderCalibrated = true;
		}
	}

	public void autonLift(int preset){
		if(preset != 0)
			Robot.intake.retract();
		goToPreset(preset);
		elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		if(elevatorPID.getError() > -Constants.ELEVATOR_PISTON_ERROR_THRESHOLD &&
				elevatorPID.getError() < Constants.ELEVATOR_PISTON_ERROR_THRESHOLD &&
				currentPresetIndex != 0)
			piston.set(Value.kForward);
		else 
			piston.set(Value.kReverse);
		
	}
	
	public boolean autonPistonExtended(){
		if(elevatorPID.getError() > -Constants.ELEVATOR_PISTON_ERROR_THRESHOLD &&
				elevatorPID.getError() < Constants.ELEVATOR_PISTON_ERROR_THRESHOLD &&
				currentPresetIndex != 0){
			piston.set(Value.kForward);
			return true;
		}
		else{
			piston.set(Value.kReverse);
			return false;
		}
	}
	
	public void run() {
		currentPreset = getSetpoint();
		
		
		// check for encoder calibration
		if(botSwitch.get()) {
			encoder.reset();
			encoderCalibrated = true;
		}
		
		// --------- swap mode --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL) && !triggeredSwap){
			triggeredSwap = true;
		}
		if(triggeredSwap && !Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			manualControl = !manualControl;
			triggeredSwap = false;
			
			
			if(!manualControl)
				currentPresetIndex = 0;
		}
		
		// --------- manual increase --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_RB)){
			triggeredInc = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_RB)){
			if(currentPresetIndex < Constants.ELEVATOR_PRESETS.length - 1)
				currentPresetIndex++;
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			if(currentPresetIndex > 0) {
				droppedTotePreset = currentPresetIndex;
				droppedToteHeight = getPresetValue(currentPresetIndex);
				droppingTote = true;
				currentPresetIndex--;
			}
			triggeredDec = false;
		}
		
		if(Robot.operator.getRawButton(Constants.BUTTON_LS)){
			droppedTotePreset = currentPresetIndex;
			droppedToteHeight = getPresetValue(currentPresetIndex);
			droppingTote = true;
			currentPresetIndex = 0;
		}
		
		
		// --------- muy importante --------- //
		if(!manualControl){
			//autoLift();
			
			if(currentPresetIndex != 0)
				Robot.intake.retract();
			goToPreset(currentPresetIndex);
			
			// if near setpoint slowdown
			if(Math.abs(elevatorPID.getError()) < Constants.ELEVATOR_SPEED_THRESHOLD) {
				elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT_CLOSE * Constants.ELEVATOR_DOWNWARDS_SCALE);
				elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT_CLOSE);
				if(currentPresetIndex > 1)
					piston.set(Value.kForward);
				else
					piston.set(Value.kReverse);
			} else {
				// if dropping tote and hook still close to where tote was dropped move slow
				if(droppingTote && Math.abs(encoder.getDistance() - droppedToteHeight) < Constants.ELEVATOR_SPEED_THRESHOLD) {
					// inert PID so max is the down direction
					elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT_CLOSE * Constants.ELEVATOR_DOWNWARDS_SCALE);
					elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT_CLOSE);
					if(droppedTotePreset > 1)
						piston.set(Value.kForward);
					else
						piston.set(Value.kReverse);
				} else {
					droppingTote = false;
					elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT);
					elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT);
					piston.set(Value.kReverse);
				}
			}
			
			elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		}
		else{
			lift.set(-Robot.operator.getAxis(Constants.AXIS_LS_Y) * Constants.ELEVATOR_SCALE);
			
			
//			if(Robot.operator.getPOV(0) == Constants.POV_RIGHT)
//				currentPreset += 1.0;
//			else if(Robot.operator.getPOV(0) == Constants.POV_LEFT)
//				currentPreset -= 1.0;
//			
//
//			goToPreset(currentPreset);
		}
		// --------- ------------------------------------------ --------- //	
		
		
		
		
		
		SmartDashboard.putNumber("Ele_Output", elevatorPID.getOutput());
	}
	
	public void reset(){
		
	}
	
	public void extend() { piston.set(Value.kForward); }
	public void retract(){ piston.set(Value.kReverse); }
	
	public void setTalon(double power)         { lift.set(power); }
	public void setOutputManual(double output) { elevatorPID.setOutputManual(output); }
	
	public double getHeight() { return encoder.getDistance(); }
	public double getPresetIndex() { return currentPresetIndex; }
	public double getPresetValue(int index) { return Constants.ELEVATOR_PRESETS[index]; }
	public double getSetpoint(){
		return elevatorPID.getSetpoint();
	}
	
	public void goToPreset(int preset) {
		currentPresetIndex = preset;
		elevatorPID.goToPreset(preset);
	}
	
	
	public void disable(){
		elevatorPID.disable();
		PID_P = Robot.dash.getDoubleFix("Ele_P", 3.00);
		PID_I = Robot.dash.getDoubleFix("Ele_I", 0.50);
		PID_D = Robot.dash.getDoubleFix("Ele_D", 0.00);
		
		elevatorPID.update_values(PID_P, PID_I, PID_D);
	}
	
	
	
	public void resetEncoder()         { encoder.reset(); }
	
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
	
	public String getControlState(){
		if(manualControl)
			return "Manual";
		else
			return "Automatic";
	}
	
	public String getPistonState(){
		if(piston.get().equals(Value.kForward))
			return "Extended";
		else if(piston.get().equals(Value.kReverse))
			return "Retracted";
		else
			return "Unknown";
	}
	
	
	public void autoLift(){
		// --------- case conditions --------- //
		if(reachedHooks[currentPresetIndex] && currentPresetIndex != 0){
			lastPresetIndex = currentPresetIndex;
			currentPresetIndex = 0;
//			hasTote = false;
		}
		if(encoder.getDistance() > elevatorPID.getPresets()[currentPresetIndex] && currentPresetIndex != 0){
			caseTimer.start();
		}
		
		if(caseTimer.get() >= 3.0){ // 3 seconds to secure on hook
			reachedHooks[currentPresetIndex] = true;
			caseTimer.stop();
			caseTimer.reset();
		}
		
		// --------- press st => start lifting --------- //
		if(Robot.driver.getRawButton(Constants.BUTTON_ST)){
			triggeredStart = true;
		}
		if(triggeredStart && !Robot.operator.getRawButton(Constants.BUTTON_ST)){
			currentPresetIndex = lastPresetIndex - 1;
//			hasTote = true;
			triggeredStart = false;
		}
	}
	
	public boolean getEncoderCalibrated() {
		return encoderCalibrated;
	}
}
