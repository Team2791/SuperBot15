package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class Intake {
	Talon leftTalon, rightTalon;
	Solenoid leftSol, rightSol;
	// state variables
	//private boolean autoMode = false;
	//private boolean haveTote = false;
	
	public Intake() {
		leftTalon = new Talon(Electronics.INTAKE_TALON_LEFT);
		rightTalon = new Talon(Electronics.INTAKE_TALON_RIGHT);
		
		leftSol = new Solenoid(Electronics.INTAKE_SOLE_LEFT);
		rightSol = new Solenoid(Electronics.INTAKE_SOLE_RIGHT);
	}
	
	public void run() {
		if(Robot.operator.getRawButton(Constants.BUTTON_Y))
			extend();
		else if(Robot.operator.getRawButton(Constants.BUTTON_A))
			retract();
		
		double input = Robot.operator.getAxis(Constants.AXIS_RS_Y);
		if(input != 0.0){
			extend();
			setSpeedManual(input,input); // test -/+, axis down = in, axis up = out
		}
	}
	
	public void extend() {
		leftSol.set(true);
		rightSol.set(true);
		leftTalon.set(Constants.INTAKE_SPEED);
		rightTalon.set(Constants.INTAKE_SPEED);
	}
	
	public void retract() {
		leftSol.set(false);
		rightSol.set(false);
		
		leftTalon.set(0.0);
		rightTalon.set(0.0);
	}
	
	public void setSpeedManual(double leftSpeed, double rightSpeed) {
		this.setSpeeds(leftSpeed, rightSpeed);
	}
	
	private void setSpeeds(double leftSpeed, double rightSpeed){ 
		leftTalon.set(leftSpeed);
		rightTalon.set(-rightSpeed);
	}
}
