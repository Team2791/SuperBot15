package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Elevator{
	// components 
	public  static Talon lift;
	public  static Gyro gyro;
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
	public int      currentPresetIndex = 0;
	public int      lastPreset         = 0;
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
		lift.set(-Robot.operator.getAxis(Constants.AXIS_LS_Y) * Constants.ELEVATOR_SCALE);
		
		//if(encoder.getDistance() > elevatorPID.getPresets()[1] * Constants.ELEVATOR_PISTON_HEIGHT_SCALE)
		//	piston.set(Value.kForward);
		//else
		//	piston.set(Value.kReverse);
		
		if(Robot.operator.getRawButton(Constants.BUTTON_LS))
			piston.set(Value.kForward);
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL))
			piston.set(Value.kReverse);
		
		if(Robot.operator.getRawButton(Constants.BUTTON_ST))
			encoder.reset();
	}
	
	public void run() {
		
		// --------- swap mode --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL) && !triggeredSwap){
			triggeredSwap = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			//manualControl = !manualControl;
			triggeredSwap = false;
		}
		
		// --------- manual increase --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredInc = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			currentPresetIndex++;
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_RB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_RB)){
			currentPresetIndex++;
			triggeredDec = false;
		}
		
		if(!manualControl)
			autoLift();
		
		

		goToPreset(currentPresetIndex);
		elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		
		if(encoder.getDistance() > elevatorPID.getPresets()[1] * Constants.ELEVATOR_PISTON_HEIGHT_SCALE)
			piston.set(Value.kForward);
		else
			piston.set(Value.kReverse);
	}
	
	public void reset(){
		for(int x = 1; x < elevatorPID.getPresets().length; x++)
			reachedHooks[x] = false;
	}
	
	public void setTalon(double power)         { lift.set(power); }
	public void setOutputManual(double output) { elevatorPID.setOutputManual(output); }
	
	public double getHeight() { return encoder.getDistance(); }
	public double getPreset() { return currentPresetIndex; }
	public double getSetpoint(){
		if(currentPresetIndex != elevatorPID.getSetpoint())
			return -1;
		else
			return elevatorPID.getSetpoint();
	}
	
	public void goToPreset(int preset) { elevatorPID.goToPreset(preset); }
	public void disable()              { elevatorPID.disable(); }
	public void resetEncoder()         { encoder.reset(); }
	
	public boolean atTop() {
		return topSwitch.get() || encoder.get() > Constants.ELEVATOR_MAX_HEIGHT - Constants.ELEVATOR_STOP_ZONE;
	}
	public boolean atBot() {
		return botSwitch.get() || encoder.get() < Constants.ELEVATOR_STOP_ZONE;
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
			lastPreset = currentPresetIndex;
			currentPresetIndex = 0;
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
			currentPresetIndex = lastPreset - 1;
			triggeredStart = false;
		}
	}
}
