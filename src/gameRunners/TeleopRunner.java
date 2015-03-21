package gameRunners;
import org.usfirst.frc.team2791.robot.*;

import subsystems.Elevator;
import subsystems.Intake;
import config.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class TeleopRunner {
	// refrences to the subsystems for easier access
	Elevator elevator = Robot.elevator;
	Intake intake = Robot.intake;
	
	// elevator control related variables
	private boolean triggeredInc  = false;
	private boolean triggeredDec  = false;
	private boolean triggeredHaveTote  = false;
	private boolean triggeredSwap = false;
	private boolean manualControl = true;
	
	public void run(){
		Robot.mDrive.run();
		Robot.dash.debug();
		Robot.intake.run();
		elevatorTeleop();
		
		Robot.compressor.start();
		Robot.dropper.run();
		
		if(Robot.driver.getRawButton(Constants.BUTTON_LB)){
			Robot.encoders.resetAll();
		}	
	}
	
	private void elevatorTeleop() {
		// --------- manual increase --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_RB)){
			triggeredInc = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_RB)){
			if(elevator.manualControl){
				if(elevator.botToteIndex < 5)
					elevator.botToteIndex++;
			} else {
				elevator.increasePreset();
			}
			//intake.retract();
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			if(!elevator.manualControl){
				elevator.goToPreset(0);
				elevator.botToteIndex = -1;
			}
		}

		if(Robot.operator.getPOV(0) == Constants.POV_RIGHT){
			elevator.manualControl = false;
		}
		if(Robot.operator.getPOV(0) == Constants.POV_LEFT){
			elevator.manualControl = true;
		}
		
		// if doing auto run tell the robot it's time to pickup a tote
		if(Robot.operator.getRawButton(Constants.BUTTON_A)){
			triggeredHaveTote = true;
		}
		if(triggeredHaveTote && !Robot.operator.getRawButton(Constants.BUTTON_A)){
			System.out.println("Manualy setting have tote to true");
			System.out.println("button A pressed");
			elevator.setToteReadyToPickup(true);
			triggeredHaveTote = false;
		}
		
		// carry out the instructions given
		elevator.runAutoLift();
		elevator.run();
	}
}
