package gameRunners;
import org.usfirst.frc.team2791.robot.*;

import config.Constants;

public class TeleopRunner {
	public TeleopRunner(){
		
	}
	
	public void init(){
		Robot.encoders.resetAll();
	}
	
	public void run(){
		//Robot.mDrive.run();
		Robot.mDrive.plainDrive();
		Robot.dash.debug();
		Robot.intake.run();
		//Robot.elevator.run();
		Robot.elevator.testRun();
		Robot.compressor.start();
		Robot.dropper.run();
		
		
		
		
		if(Robot.driver.getRawButton(Constants.BUTTON_LB)){
			Robot.encoders.resetAll();
		}	
	}
}
