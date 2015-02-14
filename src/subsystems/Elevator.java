package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
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
	
	// state variables 
	public boolean  hasTote            = false;
	public int      currentPresetIndex = 0;
	public int      lastPreset         = 0;
	private boolean triggeredInc       = false;
	private boolean triggeredDec       = false;
	private boolean triggeredStart     = false;
	private boolean reachedHooks[]     = {true, false, false, false, false};
	private Timer   caseTimer;
	
	
	public Elevator() {
		lift = new Talon(Electronics.ELEVATOR_TALON);
		
		caseTimer = new Timer();
		
		topSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_TOP);
		botSwitch = new DigitalInput(Electronics.ELEVATOR_LIM_SWITCH_BOT);
		
		encoder = new Encoder(Electronics.ELEVATOR_ENC_A, Electronics.ELEVATOR_ENC_B, false, CounterBase.EncodingType.k4X);
		encoder.setDistancePerPulse(Constants.ELEVATOR_DIST_PER_PULSE);
		

		PID_P = 3.00;
		PID_I = 0.50;
		PID_D = 0.00;
		
		elevatorPID = new ElevatorPID(PID_P, PID_I, PID_D);
		elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT);
		elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT);
	}
	
	public void run() {
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
		
		// --------- press sel => start lifting --------- //
		if(Robot.driver.getRawButton(Constants.BUTTON_SEL)){
			triggeredStart = true;
		}
		if(triggeredStart && !Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			currentPresetIndex = lastPreset - 1;
			triggeredStart = false;
		}
		
		
		goToPreset(currentPresetIndex);
		// --------- set output --------- //
		if(elevatorPID.checkPIDUse())
			elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		//else
		//	elevatorPID.setOutputManual(-Robot.operator.getAxis(Constants.AXIS_LS_Y));
	}
	
	public void reset(){
		for(int x = 1; x < elevatorPID.getPresets().length; x++)
			reachedHooks[x] = false;
	}
	
	public void setTalon(double power)         { lift.set(power); }
	public void setOutputManual(double output) { elevatorPID.setOutputManual(output); }
	
	public double getPosition()        { return encoder.getDistance(); }
	public double getSetpoint()        { return elevatorPID.getSetpoint(); }
	
	public void goToPreset(int preset) { elevatorPID.goToPreset(preset); }
	public void disable()              { elevatorPID.disable(); }
	public void resetEncoder()         { encoder.reset(); }
	
	public boolean atTop() {
		return topSwitch.get() || encoder.get() > Constants.ELEVATOR_MAX_HEIGHT - Constants.ELEVATOR_STOP_ZONE;
	}
	public boolean atBot() {
		return botSwitch.get() || encoder.get() < Constants.ELEVATOR_STOP_ZONE;
	}
}
