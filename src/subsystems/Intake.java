package subsystems;
import org.usfirst.frc.team2791.robot.Robot;
import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Talon;

public class Intake {
  private static Talon leftTalon;
  private static Talon rightTalon;
  private static DoubleSolenoid leftSol;
  private static DoubleSolenoid rightSol;

  public Intake() {
    leftTalon = new Talon(Electronics.INTAKE_TALON_LEFT);
    rightTalon = new Talon(Electronics.INTAKE_TALON_RIGHT);

    leftSol = new DoubleSolenoid(Electronics.INTAKE_SOLE_LEFT_FOR, Electronics.INTAKE_SOLE_LEFT_REV);
    rightSol = new DoubleSolenoid(Electronics.INTAKE_SOLE_RIGHT_FOR, Electronics.INTAKE_SOLE_RIGHT_REV);
  }

  public void run() {
    if (Robot.driver.getRawButton(Constants.BUTTON_A)) {
      this.retract();
    }
    if (Robot.driver.getRawButton(Constants.BUTTON_Y)) {
      this.extend();
    }

    double inputRT = Robot.driver.getAxis(Constants.AXIS_RT);
    double inputLT = Robot.driver.getAxis(Constants.AXIS_LT);
    double netInput = inputRT - inputLT;
    setSpeeds(netInput, netInput);
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

  private void setSpeeds(double leftSpeed, double rightSpeed) {
    leftTalon.set(-leftSpeed);
    rightTalon.set(rightSpeed);
  }
}
