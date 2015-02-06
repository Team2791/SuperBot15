package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.*;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Talon;

public class Elevator{
	// components 
	Talon lift;
	Gyro gyro;
	public ElevatorPID elevatorPID;
	static double PID_P, PID_I, PID_D;
	static Encoder encoder;
	static DigitalInput topSwitch, botSwitch;
	
	// state variables 
	public boolean hasTote = false;
	
	
	public Elevator() {
		lift = new Talon(Robot.eBoard.elevatorTalon);
		
		topSwitch = new DigitalInput(Robot.eBoard.elevatorLimitSwitchTop);
		botSwitch = new DigitalInput(Robot.eBoard.elevatorLimitSwitchBot);
		
		encoder = new Encoder(Robot.eBoard.elevatorEncoderA, Robot.eBoard.elevatorEncoderB, false, CounterBase.EncodingType.k4X);
		// 11.0018'' per rotation of output shaft,  12 for feet / 128 for 128
		encoder.setDistancePerPulse(11.0018 / 12.0 / 128.0);
		

		PID_P = 3.00;
		PID_I = 0.50;
		PID_D = 0.00;
		
		elevatorPID = new ElevatorPID(PID_P, PID_I, PID_D);
		elevatorPID.setMaxOutput(Constants.ELEVATOR_OUTPUT_LIMIT);
		elevatorPID.setMinOutput(-Constants.ELEVATOR_OUTPUT_LIMIT);
	}
	
	public void run() {
		if(elevatorPID.checkPIDUse())
			elevatorPID.setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		else
			elevatorPID.setOutputManual(-Robot.controls.operator.getAxis(Constants.AXIS_LS));
		
		if(Robot.controls.operator.getRawButton(Constants.BUTTON_A))
			goToPreset(0);
		else if(Robot.controls.operator.getRawButton(Constants.BUTTON_B))
			goToPreset(1);
		else if(Robot.controls.operator.getRawButton(Constants.BUTTON_X))
			goToPreset(2);
		else if(Robot.controls.operator.getRawButton(Constants.BUTTON_Y))
			goToPreset(3);
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
