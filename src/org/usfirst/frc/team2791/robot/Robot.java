
package org.usfirst.frc.team2791.robot;
import edu.wpi.first.wpilibj.IterativeRobot;
import gameRunners.*;
import subsystems.*;

public class Robot extends IterativeRobot {
	public static ElectronicsBoard eBoard;
	public static MecanumDrive mDrive;
	public static Dashboard dash;
	public static UserControls controls;
	public static TeleopRunner teleopRunner;
	public static AutonRunner autonRunner;
	public static ShakerEncoders encoders;
	public static Analyze analyzer;
	public static Elevator elevator;
	public static Intake intake;
	
	public void robotInit() {		
		eBoard   = new ElectronicsBoard();
		encoders = new ShakerEncoders();
    	controls = new UserControls();
    	dash     = new Dashboard();
    	mDrive   = new MecanumDrive();
    	analyzer = new Analyze();
    	//elevator = new Elevator();
    	intake = new Intake();
    	
    	teleopRunner = new TeleopRunner();
    	autonRunner  = new AutonRunner();
    }

	public void autonomousInit(){}
    public void autonomousPeriodic(){}

    public void teleopInit()    { teleopRunner.init();}
    public void teleopPeriodic(){
    	teleopRunner.run();
    }
    
    public void testPeriodic(){}
    
    //public void disabledInit()    { teleopRunner.init();}
    public void disabledPeriodic(){
    	Robot.dash.display();
    	if(Robot.controls.driver.getRawButton(5) || Robot.controls.operator.getRawButton(5)){
			Robot.encoders.resetAll();
			Robot.mDrive.reset();
			//Robot.elevator.resetEncoder();
		}
    	Robot.mDrive.disable();
    	//Robot.elevator.disable();
    }
    
    // public void disabledInit(){ if(INIT){ analyzer.teleopEnd(); } } // analyzer disabled
    // don't re-enable until robot is actually working because i don't know if analyzer works....
}
