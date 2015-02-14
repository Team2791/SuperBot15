package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Dropper {
	public static DoubleSolenoid rightSide;
	public static DoubleSolenoid leftSide;
	public static boolean        triggeredDrop = false;
	
	public Dropper(){
		rightSide = new DoubleSolenoid(Electronics.DROPPER_SOLE_RIGHT_DOWN, Electronics.DROPPER_SOLE_RIGHT_UP);
		leftSide  = new DoubleSolenoid(Electronics.DROPPER_SOLE_LEFT_DOWN, Electronics.DROPPER_SOLE_LEFT_UP);
	}
	
	public void run(){
		if(Robot.operator.getRawButton(Constants.BUTTON_ST))
			setPosition(Value.kForward);
		
		if(Robot.operator.getRawButton(Constants.BUTTON_SEL))
			setPosition(Value.kReverse);
	}
	
	public void setPosition(Value val){
		rightSide.set(val);
		leftSide.set(val);
	}
}
