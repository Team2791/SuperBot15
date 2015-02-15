package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Dropper {
	public static DoubleSolenoid piston;
	public static boolean        triggeredDrop = false;
	
	public Dropper(){
		piston = new DoubleSolenoid(Electronics.DROPPER_PISTON_DOWN, Electronics.DROPPER_PISTON_UP);
	}
	
	public void run(){
		if(Robot.operator.getRawButton(Constants.BUTTON_X)){
			piston.set(Value.kForward);
		}
		if(Robot.operator.getRawButton(Constants.BUTTON_Y)){
			piston.set(Value.kReverse);
		}
	}
	
	public String getState() {
		if(piston.get().equals(Value.kForward))
			return "Dropped";
		else if(piston.get().equals(Value.kReverse))
			return "Raised";
		else
			return "Unknown";
	}
}
