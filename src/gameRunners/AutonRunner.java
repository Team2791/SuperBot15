package gameRunners;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;
import edu.wpi.first.wpilibj.Timer;

public class AutonRunner {
	
	public AutonDriver driverX, driverY, driverSpin;
	private double autonX_P, autonX_I, autonX_D;
	private double autonY_P, autonY_I, autonY_D;
	private double autonSpin_P, autonSpin_I, autonSpin_D;
	private int state = 0;
	public Timer errorTimer, waitTimer;
	private int nextHook = 2;
	private boolean broken = false;
	
	public AutonRunner(){
		autonX_P = Robot.dash.getDoubleFix("autonX_P", 0.70);
		autonX_I = Robot.dash.getDoubleFix("autonX_I", 0.00);
		autonX_D = Robot.dash.getDoubleFix("autonX_D", 0.00);
		
		autonY_P = Robot.dash.getDoubleFix("autonY_P", 0.70);
		autonY_I = Robot.dash.getDoubleFix("autonY_I", 0.00);
		autonY_D = Robot.dash.getDoubleFix("autonY_D", 0.00);
		
		driverX = new AutonDriver(autonX_P, autonX_I, autonX_D);
		driverY = new AutonDriver(autonY_P, autonY_I, autonY_D);
		driverX.setMaxOutput(Constants.AUTON_DRIVE_X_PID_OUTPUT);
		driverX.setMinOutput(-Constants.AUTON_DRIVE_X_PID_OUTPUT);
		driverY.setMaxOutput(Constants.AUTON_DRIVE_Y_PID_OUTPUT);
		driverY.setMinOutput(-Constants.AUTON_DRIVE_Y_PID_OUTPUT);
		
		autonSpin_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		autonSpin_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		autonSpin_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		driverSpin = new AutonDriver(autonSpin_P, autonSpin_I, autonSpin_D);
		driverSpin.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverSpin.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
		driverSpin.setDeadzone(2);
		
		errorTimer = new Timer();
		waitTimer = new Timer();
		
		Robot.dash.getIntFix("Auton.Strat", 1);
	}
	
	private void stop() {
		driverX.setDisabled(true);
		driverY.setDisabled(true);
		Robot.intake.setSpeedManual(0.0, 0.0);
	}
	
	public void runInit(){
		broken= false;
//		driverX.driveDistance(-12.0 * 12.0); // 12 ft left
//		driverY.driveDistance(10.0 * 12.0); // 10 ft forward
				// check signs
		Robot.mDrive.gyro.reset();
		driverSpin.setTarget(0.0); // 0 degrees is what we want to be angled at
		driverX.setTarget(0.0);
		driverY.setTarget(0.0);
		errorTimer.reset();
		waitTimer.reset();
		driverX.setDisabled(false);
		driverY.setDisabled(false);
		
		errorTimer.stop();
		waitTimer.stop();
	}
	
	public void runPeriodic(){
		// first do logic		
		
		Robot.dash.debug();
		switch(Robot.dash.getIntFix("Auton.Strat", 1)){
		case 1:
			driveDiamond();
			break;
		case 2:
			pickUpOneTote();
			break;
		case 3:
			brokenAuton();
			break;
		}
		
		
		// make robot do what logic says
		if(!broken) {
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(
					-driverX.updateOutput(Robot.encoders.encoderX.getDistance()),
					driverY.updateOutput(Robot.encoders.encoderY.getDistance()),
					driverSpin.updateOutput(Robot.mDrive.gyro.getAngle()),
					0.0 // maybe 0.0
			);
		}
		
	}
	
	// autonomus modes
	private void driveDiamond() {
		// first drive 3' forward
		switch(state) {
			case 1:
				driverX.setTarget(3.0);
				driverY.setTarget(3.0);
				driverSpin.setTarget(0);
				state++;
				break;
			case 2:
				if(atTarget(false))
					state++;
				break;
			case 3:
				driverX.setTarget(6.0);
				driverY.setTarget(0.0);
				state++;
				break;
			case 4:
				if(atTarget(false))
					state++;
				break;
			case 5:
				driverX.setTarget(3.0);
				driverY.setTarget(-3.0);
				state++;
				break;
			case 6:
				if(atTarget(false))
					state++;
				break;
			case 7:
				driverX.setTarget(0.0);
				driverY.setTarget(0.0);
				state++;
				break;
			case 8:
				if(atTarget(false))
					state++;
				break;
			case 9:
				driverX.setDisabled(true);
				driverY.setDisabled(true);
				driverSpin.setTarget(360);
				state++;
				break;
			case 10:
				if(atAngle())
					state++;
				break;
			case 0:
				default:
		}
	}
	
	private void pickUpOneTote(){
		switch(state){
		case 1:
			Robot.intake.extend();
			Robot.dropper.raise();
			Robot.elevator.retract();
			state++;
			break;
		case 2:
			Robot.intake.setSpeedManual(1.0, 1.0);
			driverX.setTarget(0.0);
			driverY.setTarget(0.0);
			driverSpin.setTarget(0);
			waitTimer.start();
			state++;
			break;
		case 3:
			if(waitTimer.get() > 5.0){
				state++;
			}
			break;
		case 4:
			driverX.setDisabled(true);
			driverY.setDisabled(true);
			driverSpin.setTarget(-90.0);
			state++;
			break;
		case 5:
			if(atAngle())
				state++;
			break;
		case 6:
			driverX.setDisabled(false);
			driverY.setDisabled(false);
			driverX.setTarget(0);
			driverY.setTarget(3.0);
			state++;
			break;
		case 7:
			if(atTarget(false))
				state++;
			break;

//		case 4:
//			Robot.elevator.autonLift(2);
//			Robot.intake.setSpeedManual(0.0, 0.0);
//			state++;
//			break;
//		case 5:
//			if(waitTimer.get() > 8.0){
//				Robot.elevator.autonLift(0);
//				state++;
//			}
//			break;
//		case 6:
//			if(!Robot.elevator.autonPistonExtended()){
//				state++;
//			}
//			break;
//		case 7:
//			driverX.setDisabled(true);
//			driverY.setDisabled(true);
//			driverSpin.setTarget(90.0);
//			if(atAngle())
//				state++;
//			break;
//		case 8:
//			Robot.mDrive.gyro.reset();
//			driverX.setDisabled(false);
//			driverY.setDisabled(false);
//			if(atTarget(true))
//				state++;
//			break;
//		case 9:
//			driverY.setTarget(5.0);
//			state++;
//			break;
		case 0: default:
		}
	}
	
	
	private void brokenAuton(){
		switch(state){
		case 1:
			broken = true;
			Robot.intake.extend();
			Robot.elevator.retract();
			Robot.dropper.raise();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 2:
			if(waitTimer.get() > 2.0) {
				Robot.intake.setSpeedManual(0.2, 0.2);
				waitTimer.reset();
				state++;
			}
			break;
		case 3: // drive left
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(0, -0.5, 0.0, 0.0);
			if(waitTimer.get() > 5.0)
				state++;
		case 4:
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(0.0, 0.0, 0.0, 0.0);
			break;
		case 0: default:
			stop();
		}
	}
	
	private boolean atTarget(boolean useTimer) {
		if(useTimer) {
			if(!(Math.abs(driverX.getError()) < Constants.AUTON_DRIVE_ERROR_THRESHOLD &&
				Math.abs(driverY.getError()) < Constants.AUTON_DRIVE_ERROR_THRESHOLD)){
					errorTimer.reset();
			}
			// bump state when timer has been good for a while
			return errorTimer.get() > 0.5;
		} else {
			return (Math.abs(driverX.getError()) < 0.2 && Math.abs(driverY.getError()) < 0.2);
		}			
	}
	
	private boolean atAngle(){ return !(Math.abs(driverSpin.getError()) < Constants.AUTON_ANGLE_ERROR_THRESHOLD); }
	
	public void startAuton() { state = 1; }
	
	public void reset() {
		driverX.reset();
		driverX.reset();
		state = 0;
		Robot.encoders.resetAll();
		
		autonX_P = Robot.dash.getDoubleFix("autonX_P", 0.70);
		autonX_I = Robot.dash.getDoubleFix("autonX_I", 0.00);
		autonX_D = Robot.dash.getDoubleFix("autonX_D", 0.00);
		
		autonY_P = Robot.dash.getDoubleFix("autonY_P", 0.70);
		autonY_I = Robot.dash.getDoubleFix("autonY_I", 0.00);
		autonY_D = Robot.dash.getDoubleFix("autonY_D", 0.00);
		
		driverX.update_values(autonX_P, autonX_I, autonX_D);
		driverY.update_values(autonY_P, autonY_I, autonY_D);
		
		autonSpin_P = Robot.dash.getDoubleFix("PID_P", 0.045);
		autonSpin_I = Robot.dash.getDoubleFix("PID_I", 0.1);
		autonSpin_D = Robot.dash.getDoubleFix("PID_D", 0.01);
		
		driverSpin.update_values(autonSpin_P, autonSpin_I, autonSpin_D);
	}
	
	public int getState() { return state; }
}
