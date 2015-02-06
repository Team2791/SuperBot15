package config;
import org.usfirst.frc.team2791.robot.*;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Dashboard {
	
	Preferences pref;

	public Dashboard(){
		pref = Preferences.getInstance();
	}
	
	public void run(){
		displayJoysticks();
		displayEncoders();
		displayDrive();
		displayDrivePID();
		displayElevator();
	}
	
	public void displayJoysticks(){
		SmartDashboard.putNumber("Driver X", Robot.controls.driver.getx());
		SmartDashboard.putNumber("Driver Y", Robot.controls.driver.gety());
		SmartDashboard.putNumber("Magnitude", Robot.mDrive.getMagnitude());
		SmartDashboard.putNumber("Direction", Robot.mDrive.getDirection());
		SmartDashboard.putNumber("Spin", Robot.mDrive.getSpin());
	}
	public void displayEncoders(){
		SmartDashboard.putNumber("EncoderFL.get()", Robot.analyzer.round(Robot.encoders.encoderFL.get()));
		SmartDashboard.putNumber("EncoderBL.get()", Robot.analyzer.round(Robot.encoders.encoderBL.get()));
		SmartDashboard.putNumber("EncoderFR.get()", Robot.analyzer.round(Robot.encoders.encoderFR.get()));
		SmartDashboard.putNumber("EncoderBR.get()", Robot.analyzer.round(Robot.encoders.encoderBR.get()));
		
		SmartDashboard.putNumber("Dist from origin", Robot.encoders.getRealDistance());
		SmartDashboard.putNumber("Angle from origin", Robot.mDrive.gyro.getAngle());
		
		SmartDashboard.putString("Front Left Encoder",String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFL.getRate(),  Robot.encoders.encoderFL.getDistance()));
		SmartDashboard.putString("Front Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFR.getRate(),  Robot.encoders.encoderFR.getDistance()));
		SmartDashboard.putString("Back Left Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBL.getRate(),  Robot.encoders.encoderBL.getDistance()));
		SmartDashboard.putString("Back Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBR.getRate(),  Robot.encoders.encoderBR.getDistance()));
	}
	public void displayDrive(){
		double[] wheelSpeeds = Robot.mDrive.getWheelSpeeds();
			SmartDashboard.putString("Wheel Speeeds", String.format("FL: %f, FRL %f, BL: %f, BR: %f",
				wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]));
	}
	public void displayDrivePID(){
		SmartDashboard.putNumber("Gyro Angle", Robot.mDrive.gyro.getAngle());
		SmartDashboard.putNumber("Gyro Rate", Robot.mDrive.gyro.getRate());
		
		SmartDashboard.putNumber("PID output", Robot.mDrive.getPIDOutput());
		SmartDashboard.putNumber("Target Angle",  Robot.mDrive.getTargetAngle());
		SmartDashboard.putNumber("Gyro - Target", Robot.mDrive.gyro.getAngle() - Robot.mDrive.getTargetAngle());
	}
	public void displayElevator(){
		//SmartDashboard.putNumber("Elevator position", Robot.elevator.getPosition());
		//SmartDashboard.putNumber("Elevator setpoint", Robot.elevator.getSetpoint());
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
