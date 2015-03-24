package gameRunners;
import org.usfirst.frc.team2791.robot.*;

import subsystems.Elevator;
import subsystems.Intake;
import config.Constants;

public class TeleopRunner {
	// refrences to the subsystems for easier access
	Elevator elevator = Robot.elevator;
	Intake intake = Robot.intake;
	
	// elevator control related variables
	private boolean triggeredInc  = false;
	private boolean triggeredDec  = false;
	private boolean triggeredHaveTote  = false;
	private boolean triggeredSwapToManual = false, triggeredSwapToPreset = false, triggeredSwapToAuto = false;
	
	public void run(){
		Robot.mDrive.run();
		Robot.dash.debug();
		Robot.intake.run();
		elevatorTeleop();
		
		Robot.compressor.start();
		Robot.dropper.run();
	}
	
	public void elevatorTeleop() {
		// --------- manual increase --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_RB)){
			triggeredInc = true;
		}
		if(triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_RB)){
			if(elevator.isPresetMode())
				elevator.increasePreset();
			
			if(elevator.isAutoLift())
				elevator.setStackHeight(elevator.getStackHeight()+1);
			
			//intake.retract();
			triggeredInc = false;
		}
		
		// --------- manual decrease --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LB)){
			triggeredDec = true;
		}
		if(triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)){
			if(elevator.isPresetMode())
				elevator.goToPreset(0);
			
			if(elevator.isAutoLift())
				elevator.setStackHeight(elevator.getStackHeight()-1);
			
			triggeredDec = false;
		}
		
		// --------- manual swap modes --------- //
		if(Robot.operator.getRawButton(Constants.BUTTON_LS)){
			triggeredSwapToManual = true;
		}
		if(triggeredSwapToManual && !Robot.operator.getRawButton(Constants.BUTTON_LS)){
			elevator.manualMode = true;
			elevator.autoLiftMode = false;
			elevator.presetMode = false;
			
			elevator.setOutputManual(0.0);
			
			triggeredSwapToManual = false;
		}
		
		if(Robot.operator.getRawButton(Constants.BUTTON_ST)){
			triggeredSwapToAuto = true;
		}
		if(triggeredSwapToAuto && !Robot.operator.getRawButton(Constants.BUTTON_ST)){
			elevator.manualMode = false;
			elevator.autoLiftMode = true;
			elevator.presetMode = false;
			
			elevator.goToPreset(0);
			
			triggeredSwapToAuto = false;
		}
		
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			triggeredSwapToPreset = true;
		}
		if(triggeredSwapToPreset && !Robot.operator.getRawButton(Constants.BUTTON_SEL)){
			elevator.manualMode = false;
			elevator.autoLiftMode = false;
			elevator.presetMode = true;
			
			double tempHeight = elevator.getHeight();
			
			elevator.setTargetHeight(tempHeight);
			
			for(int c = 0; c < 5; c++){
				if(c != 5){
					if(tempHeight > elevator.getPresetValue(c) && tempHeight < elevator.getPresetValue(c+1)){
						elevator.currentPresetIndex = c;
						break;
					}
				}
				else
					elevator.currentPresetIndex = 5;
			}
			
			triggeredSwapToPreset = false;
		}		
		
		// if doing auto run tell the robot it's time to pickup a tote
		if(elevator.isAutoLift() && Robot.operator.getRawButton(Constants.BUTTON_A)){
			triggeredHaveTote = true;
		}
		if(triggeredHaveTote && !Robot.operator.getRawButton(Constants.BUTTON_A)){
			System.out.println("Manualy setting have tote to true");
			System.out.println("button A pressed");
			elevator.setToteReadyToPickup(true);
			triggeredHaveTote = false;
		}
		
		// carry out the instructions given
		elevator.newRun();
	}
}
