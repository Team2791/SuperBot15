package gameRunners;
import org.usfirst.frc.team2791.robot.*;

import config.Constants;

public class TeleopRunner {
	public TeleopRunner(){
		
	}
	
	public void init(){
		Robot.dash.dashTeleopInit();
		//Robot.analyzer.teleopInit();
		Robot.encoders.resetAll();
	}
	
	public void run(){
		Robot.mDrive.run();
		Robot.dash.display();
		//Robot.analyzer.teleopPeriodic();
		//Robot.elevator.run();
		
		if(Robot.controls.driver.getRawButton(Constants.BUTTON_LB)){
			Robot.encoders.resetAll();
		}
		
		// intake code
		Robot.intake.setSpeedManual(Robot.controls.operator.getRawAxis(2), Robot.controls.operator.getRawAxis(3));
		
		
		// calibration code
		/*if(Robot.controls.driver.getRawButton(Robot.controls.buttonX))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 135, 0);
		else if(Robot.controls.driver.getRawButton(Robot.controls.buttonY))
			Robot.mDrive.driveTrain.mecanumDrive_Polar(1, 315, 0);
		else
			Robot.mDrive.driveTrain.mecanumDrive_Polar(0, 315, 0);*/		
	}
}
