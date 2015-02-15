package config;
import org.usfirst.frc.team2791.robot.*;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Dashboard {
	Preferences pref;
	public Dashboard(){
		pref = Preferences.getInstance();
	}
	
	public void debug(){
		debugJoysticks();
		debugEncoders();
		debugDrive();
		debugDrivePID();
		debugElevatorPID();
		debugElevator();
		debugDropper();
	}
	
	public void debugJoysticks(){
		SmartDashboard.putNumber("Driver X", Robot.driver.getx());
		SmartDashboard.putNumber("Driver Y", Robot.driver.gety());
		SmartDashboard.putNumber("Spin", Robot.mDrive.getSpin());
	}
	public void debugEncoders(){		
		//SmartDashboard.putNumber("Dist from origin**", Robot.encoders.getRealDistance());
		
		//SmartDashboard.putNumber("X Distance", Robot.encoders.encoderX.getDistance());
		//SmartDashboard.putNumber("Y Distance", Robot.encoders.encoderY.getDistance());
		
		SmartDashboard.putString("Front Left Encoder",String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFL.getRate(),  Robot.encoders.encoderFL.getDistance()));
		SmartDashboard.putString("Front Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFR.getRate(),  Robot.encoders.encoderFR.getDistance()));
		SmartDashboard.putString("Back Left Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBL.getRate(),  Robot.encoders.encoderBL.getDistance()));
		SmartDashboard.putString("Back Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBR.getRate(),  Robot.encoders.encoderBR.getDistance()));
	}
	public void debugDrive(){
		double[] wheelSpeeds = Robot.mDrive.getWheelSpeeds();
			SmartDashboard.putString("Wheel Speeeds", String.format("FL: %f, FRL %f, BL: %f, BR: %f",
				wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]));
	}
	public void debugDrivePID(){
		SmartDashboard.putNumber("Gyro Angle", Robot.mDrive.gyro.getAngle());
		SmartDashboard.putNumber("Gyro Rate", Robot.mDrive.gyro.getRate());
		
		SmartDashboard.putNumber("Gyro PID output", Robot.mDrive.getPIDOutput());
		SmartDashboard.putNumber("Gyro Target",  Robot.mDrive.getTargetAngle());
		SmartDashboard.putNumber("Gyro Error Deg", Robot.mDrive.gyro.getAngle() - Robot.mDrive.getTargetAngle());
		SmartDashboard.putBoolean("Gyro Calibrating", Robot.mDrive.gyro.currentlyCalibrating());
	}
	public void debugElevatorPID(){
		SmartDashboard.putNumber("Elevator Height", Robot.elevator.getHeight());
		SmartDashboard.putNumber("Elevator rate", Robot.elevator.encoder.getRate());
		SmartDashboard.putNumber("Elevator Output", Robot.elevator.elevatorPID.getOutput());
		SmartDashboard.putNumber("Height error", Robot.elevator.getHeight() - Robot.elevator.getSetpoint());
	}
	public void debugElevator(){
		SmartDashboard.putNumber("Elevator preset", Robot.elevator.getPreset());
		SmartDashboard.putNumber("Elevator setpoint", Robot.elevator.elevatorPID.getSetpoint());
		
		SmartDashboard.putString("Elevator control", Robot.elevator.getControlState());
		SmartDashboard.putString("Elevator Piston", Robot.elevator.getPistonState());
	}
	public void debugDropper(){
		SmartDashboard.putString("Drop state", Robot.dropper.getState());
	}
	
	
	
	public int getIntFix(String key, int def){
		int val = pref.getInt(key, def);
		if(val == def)
			pref.putInt(key, val);
		return val;
	}
	public double getDoubleFix(String key, double def){
		double val = pref.getDouble(key, def);
		if(val == def)
			pref.putDouble(key, val);
		return val;
	}
}
