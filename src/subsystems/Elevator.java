package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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
	public boolean  hasTote            = false;
	public double   currentPreset      = 0;
	public int      currentPresetIndex = 0;
	public double   lastPreset         = 0;
	public int      lastPresetIndex    = 0;
	private boolean triggeredInc       = false;
	private boolean triggeredDec       = false;
	private boolean triggeredStart     = false;
	private boolean reachedHooks[]     = {true, false, false, false, false};
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
		
		piston = new DoubleSolenoid(Electronics.ELEVATOR_PISTON_FOR, Electronics.ELEVATOR_PISTON_REV);
		

		PID_P = 3.00;
		PID_I = 0.50;
		PID_D = 0.00;
		
		elevatorPID = new ElevatorPID(PID_P, PID_I, PID_D);
		elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT);
		elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT);
		
		manualControl = true;
		triggeredSwap = false;
	}
	
	public void testRun(){
		double output = -Robot.operator.getAxis(Constants.AXIS_LS_Y) * Constants.ELEVATOR_SCALE;
		
		if(atBot() && -output > 0.0)
			output = 0.0;
		else if(atTop() && -output < 0.0)
			output = 0.0;
		
		lift.set(output);
		
		//if(encoder.getDistance() > elevatorPID.getPresets()[1] * Constants.ELEVATOR_PISTON_HEIGHT_SCALE)
		//	piston.set(Value.kForward);
		//else
		//	piston.set(Value.kReverse);
		
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			if(getPistonState().equals("Unknown"))
				retract();
			else if(getPistonState().equals("Extended"))
				retract();
			else
				extend();
		}
		
		
		if(Robot.operator.getRawButton(Constants.BUTTON_ST))
			encoder.reset();
	}
	
	public void run() {
		// --------- swap mode --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL) && !triggeredSwap){
			triggeredSwap = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			manualControl = !manualControl;
			triggeredSwap = false;
		}
		
		// --------- manual increase --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_RB)){
			triggeredInc = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_RB)){
			currentPresetIndex++;
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			currentPresetIndex++;
			triggeredDec = false;
		}
		
		// --------- muy importante --------- //
		if(!manualControl)
			autoLift();
		else{
			if(Robot.operator.getPOV(0) == Constants.POV_TOP)
				currentPreset += 1.0;
			else if(Robot.operator.getPOV(0) == Constants.POV_BOT)
				currentPreset -= 1.0;
		}
		// --------- ------------------------------------------ --------- //
		
		goToPreset(currentPreset);
		elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		
		if(encoder.getDistance() > Constants.ELEVATOR_HOOK_ONE * Constants.ELEVATOR_PISTON_HEIGHT_SCALE)
			piston.set(Value.kForward);
		else
			piston.set(Value.kReverse);
	}
	
	public void reset(){
		
	}
	
	public void extend() { piston.set(Value.kForward); }
	public void retract(){ piston.set(Value.kReverse); }
	
	public void setTalon(double power)         { lift.set(power); }
	public void setOutputManual(double output) { elevatorPID.setOutputManual(output); }
	
	public double getHeight() { return encoder.getDistance(); }
	public double getPreset() { return currentPreset; }
	public double getSetpoint(){
		return elevatorPID.getSetpoint();
	}
	
	public void goToPreset(double preset) { elevatorPID.goToPreset(preset); }
	public void disable()              { elevatorPID.disable(); }
	public void resetEncoder()         { encoder.reset(); }
	
	public boolean atTop() {		
		return topSwitch.get() || encoder.get() > Constants.ELEVATOR_MAX_HEIGHT - Constants.ELEVATOR_STOP_ZONE;
	}
	public boolean atBot() {
		if(botSwitch.get() || encoder.get() < Constants.ELEVATOR_STOP_ZONE){
			encoder.reset();
			return true;
		}
		else return false;
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
			hasTote = false;
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
			hasTote = true;
			triggeredStart = false;
		}
	}
}
