package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {
	// components 
	Talon lift;
	Gyro gyro;
	public static final double[] presets = {1.0, 2.0, 3.0, 4.0}; //preset encoder values
	public ShakyPID elevatorPID;
	static double PID_P, PID_I, PID_D, PID_DEADZONE;
	static final double ELEVATOR_MAX_HEIGHT = 6.0;
	static Encoder encoder;
	static DigitalInput topSwitch, botSwitch;
	
	// state variables 
	private boolean usingPID = false;
	private boolean autoMode = false;
	private boolean haveTote = false;
	
	
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
		
		elevatorPID = new ShakyPID(PID_P, PID_I, PID_D, 0.0);
		elevatorPID.setMaxOutput(0.7);
		elevatorPID.setMinOutput(-0.7);
	}
	
	public void run() {
		if(usingPID) {
			setOutput(-elevatorPID.updateAndGetOutput(encoder.getDistance()));
		}
		if(autoMode) {
			// state machine goes here
		}
		
	}
	
	public void setOutputManual(double output) {
		usingPID = false;
		setOutput(output);
	}
	
	private void setOutput(double output) {
		// code to stop output if elevator is at top or bottom as told by limit switch and/or encoder
//		if(atTop() && output > 0.0)
//			output = 0;
//		else if(atBot() && output < 0.0)
//			output = 0;
		
		SmartDashboard.putNumber("Elevator output", output);
		lift.set(output);
	}
	
	@SuppressWarnings("unused")
	private void setOutputFeedForward(double output) {
		if(haveTote)
			setOutput(output + 0.08);
		else
			setOutput(output);
	}
	
	public double getPosition() {
		return encoder.getDistance();
	}
	
	public double getSetpoint() {
		return elevatorPID.getSetpoint();
	}
	
	public void goToPreset(int preset) {
		usingPID = true;
		elevatorPID.setSetpoint(presets[preset]);
	}
	
	public boolean atTop() {
		return topSwitch.get() || encoder.get() > ELEVATOR_MAX_HEIGHT - 0.1;
	}
	
	public boolean atBot() {
		return botSwitch.get() || encoder.get() < 0.1;
	}
	
	public void disable() {
		usingPID = false;
		elevatorPID.reset();
	}
	
	public boolean notUsingPID() { return !usingPID; }
	
	public void resetEncoder() {
		encoder.reset();
	}

}
