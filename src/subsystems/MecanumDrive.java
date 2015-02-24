package subsystems;
import org.usfirst.frc.team2791.robot.*;

import edu.wpi.first.wpilibj.SPI;
import config.Constants;
import config.DrivePID;
import config.Electronics;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import overriddenClasses.*;

public class MecanumDrive {
	public Talon frontLeft, backLeft;
	public Talon frontRight;
	public Talon backRight;
	public ShakerGyro gyro;
	public ShakerDrive driveTrain;
	
	public boolean fieldCentric = false;
	
	private double spin = 0.0;
	
	double[] wheelSpeeds = {0, 0, 0, 0};
	
	public DrivePID rotationPID;
	private double PID_P, PID_I, PID_D;
	public boolean nearAngle = false;
	public boolean PID_IN_USE = true;
	private boolean driftingFromTurn = false;
	
	// stuff for using the PID in teleop
	private double targetAngle = 0.0;
	
	public MecanumDrive(){
		frontLeft   = new Talon(Electronics.FRONT_LEFT_TALON);
		backLeft    = new Talon(Electronics.BACK_LEFT_TALON);
		frontRight  = new Talon(Electronics.FRONT_RIGHT_TALON);
		backRight   = new Talon(Electronics.BACK_RIGHT_TALON);
		
		try{
			gyro = new ShakerGyro(SPI.Port.kOnboardCS1);
			(new Thread(gyro)).start();
		}catch (InterruptedException e){
			e.printStackTrace();
		}		
		
		driveTrain  = new ShakerDrive(frontLeft, backLeft, frontRight, backRight);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		
		PID_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		PID_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		PID_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		Constants.JOYSTICK_DEG_RATE = Robot.dash.getDoubleFix("joystickMaxDegreesPerSec", 420.0);
		
		
		rotationPID = new DrivePID(PID_P, PID_I, PID_D);
		rotationPID.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		rotationPID.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
//		rotationPID.setDeadzone(2);
	}
	
	public void plainDrive(){
		driveTrain.mecanumDrive_Cartesian(Robot.driver.getx(), Robot.driver.gety(), Robot.driver.getAxis(Constants.AXIS_RS_X), 0);
	}

	public void run() {
		double driverInput;
		double xSpeed = Robot.driver.getx();
		double ySpeed = Robot.driver.gety();
		
		if(!Constants.CALIBRATION_MODE){
			// driverInput = Robot.controls.driver.getRawAxis(3) - Robot.controls.driver.getRawAxis(2); // triggers
			driverInput = Robot.driver.getAxis(Constants.AXIS_RS_X);
			if(PID_IN_USE) {
				if(driverInput != 0)
					driftingFromTurn = true;
				if(driverInput != 0 || ((Math.abs(gyro.getRate()) > 15.0) && driftingFromTurn)) {
					targetAngle = gyro.getAngle();
					rotationPID.setSetpoint(targetAngle);
					rotationPID.reset();
					spin = driverInput;
				} else {
					driftingFromTurn = false;
//					targetAngle += driverInput * Constants.JOYSTICK_DEG_RATE / 50.0;
					// add PID output and feed forward to the spin
					rotationPID.setSetpoint(targetAngle);
					
					spin = rotationPID.updateOutput(gyro.getAngle());
					// if not strafing apply daedzone to it doesnt rorate in place
					if((xSpeed ==0 && ySpeed ==0) && Math.abs(rotationPID.getError()) < 2 ) {
						// clear the I buildup
						rotationPID.reset();
						spin = 0;
					}
				}
			}
			else {
				spin = driverInput;
			}
			
			if(!fieldCentric)
				wheelSpeeds = driveTrain.mecanumDrive_Cartesian_report(xSpeed, ySpeed, spin, 0);
			else
				wheelSpeeds = driveTrain.mecanumDrive_Cartesian_report(xSpeed, ySpeed, spin, gyro.getAngle());
			
//			if(Robot.driver.getPOV(0) == Constants.POV_TOP)
//				fieldCentric = false;
//			if(Robot.driver.getPOV(0) == Constants.POV_BOT)
//				fieldCentric = true;
		}
		else
			calibrateTalons();
	}
	
	public void disable() {
    	targetAngle = gyro.getAngle();
    	
    	PID_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		PID_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		PID_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		Constants.JOYSTICK_DEG_RATE = Robot.dash.getDoubleFix("joystickMaxDegreesPerSec", 420.0);
		
		rotationPID.update_values(PID_P, PID_I, PID_D);
		rotationPID.reset();
    }
	
	public double getMagnitude(){
		double X = Robot.driver.getx();
		double Y = Robot.driver.gety();
		
		double mag = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));		
		return mag;
	}
	
	public double getDirection() { return Robot.driver.getDirectionDegrees(); }
	public double getSpin() { return spin; }
	public double[] getWheelSpeeds() { return wheelSpeeds; }
	public double getTargetAngle() { return targetAngle; }
	public double getPIDOutput() { return rotationPID.getOutput(); }
	
	
	public boolean nearSetpoint(){ return (Math.abs(rotationPID.getError()) < Constants.DRIVE_PID_ERROR_THRESHOLD); }
    public boolean nearSetpointMoving(){ return (Math.abs(rotationPID.getError()) < Constants.DRIVE_PID_ERROR_THRESHOLD_MOVING); }
    
    public void reset() {
    	gyro.reset();
    	targetAngle = 0.0;
    	rotationPID.reset();
    	rotationPID.setSetpoint(0.0);
    	
    }
    
    public String getDriveType(){
    	if(fieldCentric) return "Field Centric";
    	else return "Robot Centric";
    }
    
    public void calibrateTalons(){
    	if(Robot.driver.getRawButton(Constants.BUTTON_Y))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 45, 0);
		else if(Robot.driver.getRawButton(Constants.BUTTON_A))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 225, 0);
		else if(Robot.driver.getRawButton(Constants.BUTTON_X))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 135, 0);
		else if(Robot.driver.getRawButton(Constants.BUTTON_B))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 315, 0);
		else
			Robot.mDrive.driveTrain.mecanumDrive_Polar(0, 0, 0);
    }
}
