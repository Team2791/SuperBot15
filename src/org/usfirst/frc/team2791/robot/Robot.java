
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
	
	public void robotInit() {
		driver   = new ShakerJoystick(1);
		operator = new ShakerJoystick(2);
		
		encoders = new ShakerDriveEncoders();
    	dash     = new Dashboard();
    	mDrive   = new MecanumDrive();
    	elevator = new Elevator();
    	intake   = new Intake();
    	
    	teleopRunner = new TeleopRunner();
    	autonRunner  = new AutonRunner();
    	
    	compressro = new Compressor();
    }

	public void autonomousInit(){}
    public void autonomousPeriodic(){}

    public void teleopInit()    { teleopRunner.init();}
    public void teleopPeriodic(){
    	teleopRunner.run();
    	compressor.start();
    }
    
    public void testPeriodic(){}
    
    //public void disabledInit()    { teleopRunner.init();}
    public void disabledPeriodic(){
    	Robot.dash.run();
    	if(driver.getRawButton(5) || operator.getRawButton(5)){
			Robot.encoders.resetAll();
			Robot.mDrive.reset();
			//Robot.elevator.resetEncoder();
		}
    	Robot.mDrive.disable();
    	compressor.stop();
    	//Robot.elevator.disable();
    }
    
    // public void disabledInit(){ if(INIT){ analyzer.teleopEnd(); } } // analyzer disabled
    // don't re-enable until robot is actually working because i don't know if analyzer works....
}
