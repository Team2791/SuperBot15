package gameRunners;
import org.usfirst.frc.team2791.robot.*;

import subsystems.Elevator;
import config.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class TeleopRunner {
	// refrences to the subsystems for easier access
	Elevator elevator = Robot.elevator;;
	
	// elevator control related variables
	private boolean triggeredInc  = false;
	private boolean triggeredDec  = false;
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
			elevator.increasePreset();
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			elevator.decreasePreset();
			triggeredDec = false;
		}
		
		// drop to bottom button
		if(Robot.operator.getRawButton(Constants.BUTTON_LS)){
			elevator.goToPreset(0);
		}

		if(Robot.operator.getPOV(0) == Constants.POV_LEFT)
			elevator.manualControl = true;
		
		if(Robot.operator.getPOV(0) == Constants.POV_RIGHT)
			elevator.manualControl = false;
		
		// carry out the instructions given
		elevator.run();
	}
}
