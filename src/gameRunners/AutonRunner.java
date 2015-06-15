package gameRunners;

import org.usfirst.frc.team2791.robot.Robot;

import overriddenClasses.ShakerDrive;
import config.Constants;
import edu.wpi.first.wpilibj.Timer;

public class AutonRunner {

	public AutonDriver driverX, driverY, driverSpin;
	private double autonX_P, autonX_I, autonX_D;
	private double autonY_P, autonY_I, autonY_D;
	private double autonSpin_P, autonSpin_I, autonSpin_D;
	private int state = 0;
	public Timer errorTimer, waitTimer, autonTimer;
	private boolean broken = false;
	private double angle = Constants.AUTON_ANGLE_START_POINT;
	private double[] XY;
	
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
		autonTimer = new Timer();
		
		Robot.dash.getIntFix("Auton.Strat", 1);
	}
	
	private void stop() {
		driverX.setDisabled(true);
		driverY.setDisabled(true);
		Robot.intake.setSpeedManual(0.0, 0.0);
	}
	
	public void runInit(){
		broken= false;
		// * Robot.mDrive.gyro.reset();
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
		
		Robot.dash.gameDisplay();
		/*switch(Robot.dash.getIntFix("Auton.Strat", 1)){
		case 1:
			driveDiamond();
			break;
		case 2:
			//pickUpOneTote();
			break;
		case 3:
			brokenAuton();
			break;
		}*/
		
		//OneTote();
		//slobPush();
		//destroyedAuto();
		//driveBackFiveFt();
		
		// make robot do what logic says
		if(!broken) {
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(
					-driverX.updateOutput(Robot.encoders.encoderX.getDistance()),
					driverY.updateOutput(Robot.encoders.encoderY.getDistance()),
					0, // * driverSpin.updateOutput(Robot.mDrive.gyro.getAngle()),
					0.0 // maybe 0.0
			);
		}
		
	}
	
	// autonomous modes
	
	private void driveBackFiveFt(){
		switch(state){
		case 1:
			driverSpin.setTarget(0.0);
			driverY.setTarget(-5.0);
			driverX.setTarget(0.0);
			state++;
			break;
		case 2:
			if(atTarget(false))
				state++;
			break;
		case 0: default:
		}
		
	}
	
	private void slobAuto(){
		switch(state){
		case 1:
			driverSpin.setTarget(0.0);
			driverY.setTarget(-6);
			driverX.setTarget(0);
			state++;
			break;
		case 2:
			if(atTarget(false))
				state++;
			break;
		case 0:
			default:
		}
	}
	
	private void slobPush(){
		switch(state){
		case 1:
//			Robot.intake.extend();
			driverSpin.setTarget(0.0);
//			driverY.setTarget(0);
			driverX.setTarget(-12); // -15.3
			state++;
			break;
		case 2:
			if(atTarget(false))
				state++;
			break;
		case 0:
			default:
		}
	}
	
	private void destroyedAuto(){
		switch(state){
		case 1:
			Robot.intake.extend();
			driverSpin.setTarget(0.0);
			autonTimer.reset();
			autonTimer.start();
			driverX.setDisabled(true);
			driverY.setDisabled(true);
			state++;
			// disable normal driving
			broken = true;
			break;
		case 2:
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(0.7, 0.10, 0, 0.0);
			if(autonTimer.get() > 7.0) { // should be 7.5 for the real field
				Robot.mDrive.driveTrain.mecanumDrive_Cartesian(0.0, 0, 0, 0.0);
				state++;
			}
			break;
		case 3:
			Robot.mDrive.driveTrain.mecanumDrive_Cartesian(0.0, 0, 0, 0.0);
		case 0:
			default:
		}
	}
	
	
	
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
	
	private void OneTote(){//robot has to be set at back corner of the far right tote *human player station*\ x = = /*human player station*
		switch(state){
		case 1:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.dropper.raise();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 2:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 3:
			Robot.intake.retract();
			Robot.elevator.goToPreset(1);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 4:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				state++;
			}
			break;
		case 5:
			XY = ShakerDrive.rotate(0, -9.5, angle);
			driverY.setTarget(XY[1]);
			driverX.setTarget(XY[0]);
			driverSpin.setTarget(0);
			state++;
			break;
		case 6:
			if(atTarget(false))
				state++;
			break;
		case 7:
			if(atAngle())
				Robot.elevator.goToPreset(0);
			break;
		case 0: default:
			stop();
		}
	}
	private void TwoTote(){//robot has to be set at back corner of the middle tote *human player staion*/= x =\*human player staion*
		switch(state){
		case 1:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.dropper.raise();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 2:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 3:
			Robot.intake.retract();
			Robot.elevator.goToPreset(2);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 4:
			if(waitTimer.get() > 2.5){
				Robot.elevator.goToPreset(0);
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 5:
			XY = ShakerDrive.rotate(5, 0, angle);
			driverY.setTarget(XY[1]);
			driverX.setTarget(XY[0]);
			driverSpin.setTarget(0);
			state++;
			break;
		case 6:
			if(atTarget(false)){
				XY = ShakerDrive.rotate(0, 1, angle);
				driverY.setTarget(XY[1]);
				driverX.setTarget(XY[0]);
				driverSpin.setTarget(0);
				state++;
			}
			break;
		case 7:
			if(atTarget(false)){
				state++;
			}
			break;
		case 8:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 9:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 10:
			Robot.intake.retract();
			Robot.elevator.goToPreset(1);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 11:
			if(waitTimer.get() > 2.0){
				waitTimer.reset();
				waitTimer.stop();
				XY = ShakerDrive.rotate(0, -9.5, angle);
				driverY.setTarget(XY[1]);
				driverX.setTarget(XY[0]);
				driverSpin.setTarget(0);
				state++;
			}
			break;
		case 12:
			if(atTarget(false)){
				state++;	
			}
			break;
		case 13:
			Robot.dropper.drop();
			state++;
			break;
		case 0: default:
			stop();
		}
	}
	private void ThreeTote(){//robot has to be set at back corner of the left tote when looking from middle of field*human player staion*/x = =\*human player staion*
		switch(state){
		case 1:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.dropper.raise();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 2:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 3:
			Robot.intake.retract();
			Robot.elevator.goToPreset(3);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 4:
			if(waitTimer.get() > 2.75){
				Robot.elevator.goToPreset(0);
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 5:
			XY = ShakerDrive.rotate(5, 0, angle);
			driverY.setTarget(XY[1]);
			driverX.setTarget(XY[0]);
			driverSpin.setTarget(0);
			state++;
			break;
		case 6:
			if(atTarget(false)){
				XY = ShakerDrive.rotate(0, 1, angle);
				driverY.setTarget(XY[1]);
				driverX.setTarget(XY[0]);
				driverSpin.setTarget(0);
				state++;
			}
			break;
		case 7:
			if(atTarget(false)){
				state++;
			}
			break;
		case 8:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 9:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 10:
			Robot.intake.retract();
			Robot.elevator.goToPreset(2);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 11:
			if(waitTimer.get() > 2.5){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 12:
			XY = ShakerDrive.rotate(5, 0, angle);
			driverY.setTarget(XY[1]);
			driverX.setTarget(XY[0]);
			driverSpin.setTarget(0);
			state++;
			break;
		case 13:
			if(atTarget(false)){
				XY = ShakerDrive.rotate(0, 1, angle);
				driverY.setTarget(XY[1]);
				driverX.setTarget(XY[0]);
				driverSpin.setTarget(0);
				state++;
			}
			break;
		case 14:
			if(atTarget(false)){
				state++;
			}
			break;
		case 15:
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 16:
			if(waitTimer.get() > 3.0){
				waitTimer.reset();
				waitTimer.stop();
				state++;
			}
			break;
		case 17:
			Robot.intake.retract();
			Robot.elevator.goToPreset(1);
			driverSpin.setTarget(0.0);
			waitTimer.start();
			state++;
			break;
		case 18:
			if(waitTimer.get() > 2.0){
				waitTimer.reset();
				waitTimer.stop();
				XY = ShakerDrive.rotate(0, -9.5, angle);
				driverY.setTarget(XY[1]);
				driverX.setTarget(XY[0]);
				driverSpin.setTarget(0);
				state++;
			}
			break;
		case 19:
			if(atTarget(false)){
				state++;	
			}
			break;
		case 20:
			Robot.dropper.drop();
			state++;
			break;
		case 0: default:
			stop();
		}
	}
	
	private void OneCan(){//intake is set up touching the can either straight on or at an angle
		switch(state){
		case 1:
			Robot.intake.extend();
			Robot.intake.setSpeedManual(1.0, 1.0);
			waitTimer.start();
			state++;
			break;
		case 2:
			if(waitTimer.get() > 2.5){
				waitTimer.reset();
				state++;
			}
			break;
		case 3:
			Robot.intake.setSpeedManual(0.3, 0.3);//can be adjusted based on the ability of intake to hold the can
			XY = ShakerDrive.rotate(0, -9.5, angle);
			driverY.setTarget(XY[1]);
			driverX.setTarget(XY[0]);
			driverSpin.setTarget(0);
			state++;
			break;
		case 4:
			if(atTarget(false)){
				Robot.intake.retract();
				state++;
			}
			break;
		case 0: default:
			stop();	
		}
	}
	
	private void brokenAuton(){
		switch(state){
		case 1:
			broken = true;
			Robot.intake.extend();
			Robot.elevator.retractTotePiston();
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
