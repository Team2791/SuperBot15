package gameRunners;

public class AutonRunner {
	
	private AutonRunner driverX, driverY;
	private double autonX_P, autonX_I, autonX_D;
	private double autonY_P, autonY_I, autonY_D;
	
	public AutonRunner(){
		autonX_P = Robot.dash.getDoubleFix("autonX_P", 0.045);
		autonX_I = Robot.dash.getDoubleFix("autonX_I", 0.1);
		autonX_D = Robot.dash.getDoubleFix("autonX_D", 0.01);
		
		autonY_P = Robot.dash.getDoubleFix("autonY_P", 0.045);
		autonY_I = Robot.dash.getDoubleFix("autonY_I", 0.1);
		autonY_D = Robot.dash.getDoubleFix("autonY_D", 0.01);
		
		driverX = new AutonRunner(autonX_P, autonX_I, autonX_D);
		driverY = new AutonRunner(autonY_P, autonY_I, autonY_D);
		driverX.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverX.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
		driverY.setMaxOutput(Constants.DRIVE_PID_OUTPUT);
		driverY.setMinOutput(-Constants.DRIVE_PID_OUTPUT);
	}
	
	public void run(){
		Robot.mDrive.driveTrain.mecanumDrive_Cartesian(driverX.updateOutput(encoderX), driverY.updateOutput(encoderY), 0, Robot.mDrive.gyro.getAngle());
	}
}
