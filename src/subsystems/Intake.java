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
		
		double inputRT = Robot.driver.getAxis(Constants.AXIS_RT);
		double inputLT = Robot.driver.getAxis(Constants.AXIS_LT);
		double netInput = inputRT - inputLT;
		setSpeeds(netInput, netInput);
		
		
		/*//double inputY = Robot.operator.getAxis(Constants.AXIS_RS_Y);
		//double inputX = Robot.operator.getAxis(Constants.AXIS_RS_X);
		
		double inputRT = Robot.driver.getAxis(Constants.AXIS_RT);
		double inputLT = Robot.driver.getAxis(Constants.AXIS_LT);
		
		double inputOpRT = Robot.operator.getAxis(Constants.AXIS_RT);
		double inputOpLT = Robot.operator.getAxis(Constants.AXIS_LT);
		
		double inputOpRS = Robot.operator.getAxis(Constants.AXIS_RS_X);
		double inputOpRSY = Robot.operator.getAxis(Constants.AXIS_RS_Y);
		double netInput = 0;
		
		if(Math.abs(inputOpRS) >= 0.15 && Math.abs(inputOpRSY) <= .15){
			if(inputOpRS < 0)
				setSpeedManual(0, -inputOpRS);
			else if(inputOpRS > 0)
				setSpeedManual(inputOpRS, 0);
		}
		else if (Math.abs(inputOpRSY) >= .15){
			setSpeedManual(inputOpRSY, inputOpRSY);
		}
		else{
			netInput = inputRT - inputLT + (inputOpRT - inputOpLT);
			setSpeedManual(netInput, netInput);
		}*/
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
