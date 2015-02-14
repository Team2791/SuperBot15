package gameRunners;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;

public class AutonRunner {
	
	private AutonDriver driverX, driverY, driverSpin;
	private double autonX_P, autonX_I, autonX_D;
	private double autonY_P, autonY_I, autonY_D;
	private double autonSpin_P, autonSpin_I, autonSpin_D;
	
	public AutonRunner(){
		autonX_P = Robot.dash.getDoubleFix("autonX_P", 0.045);
		autonX_I = Robot.dash.getDoubleFix("autonX_I", 0.1);
		autonX_D = Robot.dash.getDoubleFix("autonX_D", 0.01);
		
		autonY_P = Robot.dash.getDoubleFix("autonY_P", 0.045);
		autonY_I = Robot.dash.getDoubleFix("autonY_I", 0.1);
		autonY_D = Robot.dash.getDoubleFix("autonY_D", 0.01);
		
		driverX = new AutonDriver(autonX_P, autonX_I, autonX_D);
		driverY = new AutonDriver(autonY_P, autonY_I, autonY_D);
		driverX.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverX.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
		driverY.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverY.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
		
		autonSpin_P = Robot.dash.getDoubleFix("autonSpin_P", 0.045);
		autonSpin_I = Robot.dash.getDoubleFix("autonSpin_P", 0.1);
		autonSpin_D = Robot.dash.getDoubleFix("autonSpin_P", 0.01);
		
		driverSpin = new AutonDriver(autonSpin_P, autonSpin_I, autonSpin_D);
		driverSpin.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverSpin.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
	}
	
	public void runInit(){
		driverX.driveDistance(-12.0 * 12.0); // 12 ft left
		driverY.driveDistance(10.0 * 12.0); // 10 ft forward
				// check signs
		driverSpin.setTarget(0.0); // 0 degrees is what we want to be angled at
	}
	
	public void runPeriodic(){
		Robot.mDrive.driveTrain.mecanumDrive_Cartesian(
				driverX.updateOutput(Robot.encoders.encoderX.getDistance()),
				driverY.updateOutput(Robot.encoders.encoderY.getDistance()),
				driverSpin.updateOutput(Robot.mDrive.gyro.getAngle()),
				Robot.mDrive.gyro.getAngle() // maybe 0.0
		);
	}
}
