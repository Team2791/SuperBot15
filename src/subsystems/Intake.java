package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Talon;

public class Intake {
	public static Talon          leftTalon;
	public static Talon          rightTalon;
	public static DoubleSolenoid leftSol;
	public static DoubleSolenoid rightSol;
	
	public Intake() {
		leftTalon  = new Talon(Electronics.INTAKE_TALON_LEFT);
		rightTalon = new Talon(Electronics.INTAKE_TALON_RIGHT);
		
		leftSol    = new DoubleSolenoid(Electronics.INTAKE_SOLE_LEFT_FOR, Electronics.INTAKE_SOLE_LEFT_REV);
		rightSol   = new DoubleSolenoid(Electronics.INTAKE_SOLE_RIGHT_FOR, Electronics.INTAKE_SOLE_RIGHT_REV);
	}
	
	public void run() {
		if(Robot.driver.getRawButton(Constants.BUTTON_A))
			this.retract();
		if(Robot.driver.getRawButton(Constants.BUTTON_Y)){
			this.extend();
			Robot.dropper.raise();
		}
		
		//double inputY = Robot.operator.getAxis(Constants.AXIS_RS_Y);
		//double inputX = Robot.operator.getAxis(Constants.AXIS_RS_X);
		
		double inputRT = Robot.driver.getAxis(Constants.AXIS_RT);
		double inputLT = Robot.driver.getAxis(Constants.AXIS_LT);
		
		double netInput = inputRT - inputLT;
		
		// check signs. rs-> = intake right side, rs<- = intake left side
			// maybe swap to get clockwise/counterclockwise bin rotation
		/*if(inputX > Constants.INTAKE_DEADZONE)
			setSpeedManual(0.0, inputX);
		else if(inputX < -Constants.INTAKE_DEADZONE)
			setSpeedManual(-inputX, 0.0);
		else
			setSpeedManual(inputY,inputY);*/
		
		setSpeedManual(netInput, netInput);
	}
	
	public void extend() {
		Robot.dropper.raise();
		leftSol.set(Value.kForward);
		rightSol.set(Value.kForward);
	}
	
	public void retract() {
		leftSol.set(Value.kReverse);
		rightSol.set(Value.kReverse);
		
		leftTalon.set(0.0);
		rightTalon.set(0.0);
	}

	public void setSpeedManual(double leftSpeed, double rightSpeed) {
		this.setSpeeds(leftSpeed, rightSpeed);
	}
	
	private void setSpeeds(double leftSpeed, double rightSpeed){ 
		leftTalon.set(-leftSpeed);
		rightTalon.set(rightSpeed);
	}
	
	public String getPistonState(){
		if(leftSol.get().equals(Value.kReverse))
			return "Retracted";
		else if(leftSol.get().equals(Value.kForward))
			return "Extended";
		else
			return "Unknown";
	}
}
