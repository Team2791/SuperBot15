package gameRunners;
import org.usfirst.frc.team2791.robot.*;

public class TeleopRunner {
	public TeleopRunner(){
		
	}
	
	public void init(){
		Robot.dash.dashTeleopInit();
		//Robot.analyzer.teleopInit();
		Robot.encoders.resetAll();
	}
	
	public void run(){
		Robot.mDrive.drive();
		Robot.dash.display();
		//Robot.analyzer.teleopPeriodic(); 
		
		if(Robot.controls.driver.getRawButton(5)){
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
		
		
		/*
		if(Math.abs(Robot.encoders.encoderFL.get()) >= 128 * 8){
			Robot.setINIT(false);
		} */
		
		// ELEVATOR STUFF{
		// elevator telop
		/*double driverInput = -Robot.controls.operator.getRawAxis(1);
		if(driverInput < -0.1 || driverInput > 0.1 || Robot.elevator.notUsingPID())
			Robot.elevator.setOutputManual(driverInput);*/
		
	
		
		/*if(Robot.controls.operator.getRawButton(Robot.controls.buttonB))
			Robot.elevator.goToPreset(0);
		else if(Robot.controls.operator.getRawButton(Robot.controls.buttonY))
			Robot.elevator.goToPreset(1);
		else if(Robot.controls.operator.getRawButton(Robot.controls.buttonX))
			Robot.elevator.goToPreset(2);
		else if(Robot.controls.operator.getRawButton(Robot.controls.buttonA))
			Robot.elevator.goToPreset(3);*/
		
		//Robot.elevator.run();
		// }
		
	}
}
