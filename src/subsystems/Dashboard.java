package subsystems;
import org.usfirst.frc.team2791.robot.*;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Dashboard {
	int[] jSettings;
	Preferences pref;

	public Dashboard(){
		pref = Preferences.getInstance();
		
		jSettings = new int[11];
		jSettings[1]  = getIntFix("buttonA",	 1);
		jSettings[2]  = getIntFix("buttonB",	 2);
		jSettings[3]  = getIntFix("buttonX",	 3);
		jSettings[4]  = getIntFix("buttonY",	 4);
		jSettings[5]  = getIntFix("buttonLB",    5);
		jSettings[6]  = getIntFix("buttonRB",    6);
		jSettings[7]  = getIntFix("buttonSel",   7);
		jSettings[8]  = getIntFix("buttonStart", 8);
		jSettings[9]  = getIntFix("buttonLT",    9);
		jSettings[10] = getIntFix("buttonRT",    10);
		Robot.controls.updateControls(jSettings);
	}
	
	public void display(){
		SmartDashboard.putNumber("Driver X", Robot.controls.driver.getx());
		SmartDashboard.putNumber("Driver Y", Robot.controls.driver.gety());
		SmartDashboard.putNumber("Magnitude", Robot.mDrive.getMagnitude());
		SmartDashboard.putNumber("Direction", Robot.mDrive.getDirection());
		SmartDashboard.putNumber("Spin", Robot.mDrive.getSpin());

		SmartDashboard.putNumber("EncoderFL.get()", Robot.analyzer.round(Robot.encoders.encoderFL.get()));
		SmartDashboard.putNumber("EncoderBL.get()", Robot.analyzer.round(Robot.encoders.encoderBL.get()));
		SmartDashboard.putNumber("EncoderFR.get()", Robot.analyzer.round(Robot.encoders.encoderFR.get()));
		SmartDashboard.putNumber("EncoderBR.get()", Robot.analyzer.round(Robot.encoders.encoderBR.get()));
		
		SmartDashboard.putNumber("Test dist traveled", Robot.encoders.getRealDistance());
		
		
		SmartDashboard.putString("Front Left Encoder",String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFL.getRate(),  Robot.encoders.encoderFL.getDistance()));
		SmartDashboard.putString("Front Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderFR.getRate(),  Robot.encoders.encoderFR.getDistance()));
		SmartDashboard.putString("Back Left Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBL.getRate(),  Robot.encoders.encoderBL.getDistance()));
		SmartDashboard.putString("Back Right Encoder.", String.format("Speed: %f Distance:%f\n", Robot.encoders.encoderBR.getRate(),  Robot.encoders.encoderBR.getDistance()));
	
		SmartDashboard.putNumber("Gyro Angle", Robot.mDrive.gyro.getAngle());
		SmartDashboard.putNumber("Gyro Rate", Robot.mDrive.gyro.getRate());
		
		SmartDashboard.putNumber("PID output", Robot.mDrive.getPIDOutput());
		SmartDashboard.putNumber("Target Angle",  Robot.mDrive.getTargetAngle());
		SmartDashboard.putNumber("Gyro - Target", Robot.mDrive.gyro.getAngle() - Robot.mDrive.getTargetAngle());
		
		
		
		
		
		double[] wheelSpeeds = Robot.mDrive.getWheelSpeeds();
			SmartDashboard.putString("Wheel Speeeds", String.format("FL: %f, FRL %f, BL: %f, BR: %f",
								wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]));
		
		//SmartDashboard.putNumber("Elevator position", Robot.elevator.getPosition());
		//SmartDashboard.putNumber("Elevator setpoint", Robot.elevator.getSetpoint());
		
	}
	
	public void dashTeleopInit(){
		jSettings[1]  = getIntFix("buttonA",	 1);
		jSettings[2]  = getIntFix("buttonB",	 2);
		jSettings[3]  = getIntFix("buttonX",	 3);
		jSettings[4]  = getIntFix("buttonY",	 4);
		jSettings[5]  = getIntFix("buttonLB",    5);
		jSettings[6]  = getIntFix("buttonRB",    6);
		jSettings[7]  = getIntFix("buttonSel",   7);
		jSettings[8]  = getIntFix("buttonStart", 8);
		jSettings[9]  = getIntFix("buttonLT",    9);
		jSettings[10] = getIntFix("buttonRT",    10);
		Robot.controls.updateControls(jSettings);
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
