package subsystems;

import org.usfirst.frc.team2791.robot.Robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class Intake {
	Talon leftTalon, rightTalon;
	Solenoid leftSol, rightSol;
	public static final double TALON_SPEED = 0.5; //Arbitrary - don't know yet.
	// state variables
	private boolean autoMode = false;
	private boolean haveTote = false;
	
	public Intake() {
		leftTalon = new Talon(Robot.eBoard.intakeTalonLeft);
		rightTalon = new Talon(Robot.eBoard.intakeTalonRight);
		
		leftSol = new Solenoid(Robot.eBoard.intakeSolPortLeft);
		rightSol = new Solenoid(Robot.eBoard.intakeSolPortRight);
	}
	
	public void run() {}
	
	public void extend() {
		leftSol.set(true);
		rightSol.set(true);
		leftTalon.set(TALON_SPEED);
		rightTalon.set(TALON_SPEED);
	}
	
	public void retract() {
		leftSol.set(false);
		rightSol.set(false);
		
		leftTalon.set(0.0);//corrected by AKHIL 
		rightTalon.set(0.0);
	}
	
	public void setSpeedManual(double leftSpeed, double rightSpeed) {
		this.setSpeeds(leftSpeed, rightSpeed);
	}
	
	private void setSpeeds(double leftSpeed, double rightSpeed) {
//		if(leftSpeed < 0.0) {
//			leftTalon.set(leftSpeed);
//		}
//		if(rightSpeed < 0.0) {
//			rightTalon.set(rightSpeed);
//		}
//		if(leftSpeed >= 0.0 && rightSpeed >= 0.0) {
			leftTalon.set(leftSpeed);
			rightTalon.set(-rightSpeed);
//		}
	}
}
