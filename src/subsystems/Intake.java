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
	// state variables
	//private boolean autoMode = false;
	//private boolean haveTote = false;
	
	public Intake() {
		leftTalon  = new Talon(Electronics.INTAKE_TALON_LEFT);
		rightTalon = new Talon(Electronics.INTAKE_TALON_RIGHT);
		
		leftSol    = new DoubleSolenoid(Electronics.INTAKE_SOLE_LEFT_FOR, Electronics.INTAKE_SOLE_LEFT_REV);
		rightSol   = new DoubleSolenoid(Electronics.INTAKE_SOLE_RIGHT_FOR, Electronics.INTAKE_SOLE_RIGHT_REV);
	}
	
	public void run() {
//		if(Robot.operator.getRawButton(Constants.BUTTON_A)){
//			if(getPistonState().equals("Unknown"))
//				retract();
//			else if(getPistonState().equals("Extended"))
//				retract();
//			else
//				extend();
//		}
//		
//		if(Robot.operator.getRawButton(Constants.BUTTON_X)){
//			if(getPistonState(leftSol).equals("Unknown"))
//				retract(leftSol);
//			else if(getPistonState(leftSol).equals("Extended"))
//				retract(leftSol);
//			else
//				extend(leftSol);
//		}
//		
//		if(Robot.operator.getRawButton(Constants.BUTTON_B)){
//			if(getPistonState(rightSol).equals("Unknown"))
//				retract(rightSol);
//			else if(getPistonState(rightSol).equals("Extended"))
//				retract(rightSol);
//			else
//				extend(rightSol);
//		}
		
		
		if(Robot.operator.getRawButton(Constants.BUTTON_A))
			this.retract();
		if(Robot.operator.getRawButton(Constants.BUTTON_Y))
			this.extend();
		
		
		
		
		
		double inputY = Robot.operator.getAxis(Constants.AXIS_RS_Y);
		double inputX = Robot.operator.getAxis(Constants.AXIS_RS_X);
		
		// check signs. rs-> = intake right side, rs<- = intake left side
			// maybe swap to get clockwise/counterclockwise bin rotation
		if(inputX > Constants.INTAKE_DEADZONE)
			setSpeedManual(0.0, inputX);
		else if(inputX < -Constants.INTAKE_DEADZONE)
			setSpeedManual(-inputX, 0.0);
		else
			setSpeedManual(inputY,inputY);		
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
	
	public void extend(DoubleSolenoid sol) { sol.set(Value.kForward); }
	public void retract(DoubleSolenoid sol){ sol.set(Value.kReverse); }
	
	public void setSpeedManual(double leftSpeed, double rightSpeed) {
		this.setSpeeds(leftSpeed, rightSpeed);
	}
	
	private void setSpeeds(double leftSpeed, double rightSpeed){ 
		leftTalon.set(leftSpeed);
		rightTalon.set(-rightSpeed);
	}
	
	public String getPistonState(){
		if(leftSol.get().equals(Value.kReverse))
			return "Retracted";
		else if(leftSol.get().equals(Value.kForward))
			return "Extended";
		else
			return "Unknown";
	}
	public String getPistonState(DoubleSolenoid sol){
		if(sol.get().equals(Value.kReverse))
			return "Retracted";
		else if(sol.get().equals(Value.kForward))
			return "Extended";
		else
			return "Unknown";
	}
}
