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
		/*if(Robot.operator.getRawButton(Constants.BUTTON_Y)){
			if(getState().equals("Unknown"))
				raise();
			else if(getState().equals("Raised"))
				drop();
			else
				raise();
		}*/
		
		//drop();
		
		
		if(Robot.operator.getPOV(0) == Constants.POV_TOP)
			this.raise();
		if(Robot.operator.getPOV(0) == Constants.POV_BOT){
			this.drop();
			Robot.elevator.goToPreset(0);
		}
		
		
		
		
	}
	
	public String getState() {
		if(piston.get().equals(Value.kForward))
			return "Raised";
		else if(piston.get().equals(Value.kReverse))
			return "Dropped";
		else
			return "Unknown";
	}
	
	public void raise(){ piston.set(Value.kForward); }
	public void drop() {
		Robot.intake.retract();
		piston.set(Value.kReverse);
	}
}
