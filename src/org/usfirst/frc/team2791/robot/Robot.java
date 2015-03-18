
package org.usfirst.frc.team2791.robot;
import overriddenClasses.ShakerJoystick;
import config.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Compressor;
import gameRunners.*;
import subsystems.*;

public class Robot extends IterativeRobot {
	public static MecanumDrive mDrive;
	public static Dashboard dash;
	public static TeleopRunner teleopRunner;
	public static AutonRunner autonRunner;
	public static ShakerDriveEncoders encoders;
	public static Elevator elevator;
	public static Intake intake;
	public static ShakerJoystick driver, operator;
	public static Compressor compressor;
	public static Dropper dropper;
	public static ShakerCamera camera;
	
	public double angleChange = 0.0;
	
	public void robotInit() {
		driver   = new ShakerJoystick(0);
		operator = new ShakerJoystick(1);
		
		compressor = new Compressor();
		encoders = new ShakerDriveEncoders();
    	dash     = new Dashboard();
    	mDrive   = new MecanumDrive();
    	elevator = new Elevator();
    	intake   = new Intake();
    	dropper  = new Dropper();
    	
    	teleopRunner = new TeleopRunner();
    	autonRunner  = new AutonRunner();
//    	camera = new ShakerCamera();
	}	

	public void autonomousInit(){
		autonRunner.reset();
		autonRunner.runInit();
		autonRunner.startAuton();
	}
    public void autonomousPeriodic(){
		dash.debug();
    	autonRunner.runPeriodic();
    }

    public void teleopPeriodic(){
    	teleopRunner.run();
    }
    
    public void testPeriodic(){}
    
    public void disabledInit() {
    	autonRunner.reset();
    }
    public void disabledPeriodic(){
    	compressor.stop();
    	mDrive.disable();
    	elevator.disable();
    	
    	if(operator.getRawButton(Constants.BUTTON_SEL) || driver.getRawButton(Constants.BUTTON_SEL)){
    		mDrive.gyro.recalibrate();
    		elevator.resetEncoder();
    		encoders.encoderX.reset();
    		encoders.encoderY.reset();
    	}
    	
    	Robot.dash.debug();
    }
}
