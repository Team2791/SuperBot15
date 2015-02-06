package subsystems;
import org.usfirst.frc.team2791.robot.*;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import overriddenClasses.*;

public class MecanumDrive {
	public Talon frontLeft, backLeft;
	public Talon frontRight;
	public Talon backRight;
	public Gyro gyro; // not used on test board
	public ShakerDrive driveTrain;
	public static final double scale_factor = 0.35;
	private double spin = 0.0;
	// this is for debugging
	double[] wheelSpeeds = {0, 0, 0, 0};
	
	public ShakyPID rotationPID;
	private double PID_P, PID_I, PID_D, PID_DEADZONE;
	private static final double[] PRESETS = {0, 90, 180, 270};
	public boolean nearAngle = false;
	public boolean PID_IN_USE = true;
	
	// stuff for using the PID in teleop
	private double targetAngle = 0.0, joystickMaxDegreesPerSec = 420.0;
			
	
	public MecanumDrive(){
		frontLeft   = new Talon(Robot.eBoard.frontLeftTalon);
		backLeft    = new Talon(Robot.eBoard.backLeftTalon);
		frontRight  = new Talon(Robot.eBoard.frontRightTalon);
		backRight   = new Talon(Robot.eBoard.backRightTalon);
		
		gyro = new Gyro(Robot.eBoard.gyro);
		gyro.setSensitivity(0.002); //also try 0.00195 
		
		driveTrain  = new ShakerDrive(frontLeft, backLeft, frontRight, backRight);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		
		PID_DEADZONE = 0;
		
		PID_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		PID_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		PID_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		joystickMaxDegreesPerSec = Robot.dash.getDoubleFix("joystickMaxDegreesPerSec", 420.0);
		
		
		rotationPID = new ShakyPID(PID_P, PID_I, PID_D, PID_DEADZONE);
		rotationPID.setMaxOutput(.3);
		rotationPID.setMinOutput(-.3);
	}
	public void drive() {
		// combine the inputs from both triggers into a single number
		double driverInput;
		// triggers 
//		driverInput = Robot.controls.driver.getRawAxis(3) - Robot.controls.driver.getRawAxis(2);
		// right stick X
		driverInput = Robot.controls.driver.getRawAxis(4);
		
		// 0.05 deeadzone
		if(driverInput < 0.05 && driverInput > -0.05)
			driverInput = 0;
		
		if(PID_IN_USE) {
			targetAngle += driverInput * joystickMaxDegreesPerSec / 50.0;
			rotationPID.setSetpoint(targetAngle);
			// add PID output and feed forward to the spin
			spin = -rotationPID.updateOutput(gyro.getAngle()) + driverInput;
		}
		else {
			spin = driverInput;
		}
		
		//driveTrain.mecanumDrive_Polar(getMagnitude(), getDirection(), getSpin());
		wheelSpeeds = driveTrain.mecanumDrive_Cartesian_report(Robot.controls.driver.getx(), Robot.controls.driver.gety(), spin, 0);
	}
	
	public void disable() {
    	targetAngle = gyro.getAngle();
    	
    	PID_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		PID_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		PID_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		joystickMaxDegreesPerSec = Robot.dash.getDoubleFix("joystickMaxDegreesPerSec", 420.0);
		
		rotationPID.update_values(PID_P, PID_I, PID_D, PID_DEADZONE);
		rotationPID.reset();
    }
	public void goToPreset(int preset){
		if(preset < 0 || preset >= PRESETS.length) return;
		
		PID_IN_USE = true;
		rotationPID.setSetpoint(PRESETS[preset]);
		rotationPID.reset();
	}
	
	public double getMagnitude(){
		double X = Robot.controls.driver.getx();
		double Y = Robot.controls.driver.gety();
		
		double mag = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));		
		return mag;
	}
	
	public double getDirection() { return Robot.controls.driver.getDirectionDegrees(); }
	public double getSpin() { return spin; }
	public double[] getWheelSpeeds() { return wheelSpeeds; }
	public double getTargetAngle() { return targetAngle; }
	public double getPIDOutput() { return rotationPID.getOutput(); }
	
	
	public boolean nearSetpoint(){ return (Math.abs(rotationPID.getError()) < 1.5); }
    public boolean nearSetpointMoving(){ return (Math.abs(rotationPID.getError()) < 5.0); }
    
    public void reset() {
    	gyro.reset();
    	targetAngle = 0.0;
    }
}
