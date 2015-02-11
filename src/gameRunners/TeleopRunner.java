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
		Robot.mDrive.run();
		Robot.dash.run();
		Robot.intake.run();
		Robot.elevator.run();
		
		if(Robot.driver.getRawButton(Constants.BUTTON_LB)){
			Robot.encoders.resetAll();
		}	
	}
}
