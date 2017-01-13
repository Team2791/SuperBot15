package overriddenClasses;
import config.Constants;
import edu.wpi.first.wpilibj.Joystick;

public class ShakerJoystick extends Joystick {
  private static double JOYSTICK_SCALE = Constants.JOYSTICK_SCALE;

  public ShakerJoystick(final int port) {
    super(port);
  }

  public double getx() {
    double X = super.getX();
    return fixXYInput(X);
  }

  public double gety() {
    double Y = super.getY();
    return fixXYInput(Y);
  }

  public double getAxis(int slot) {
    double input = super.getRawAxis(slot);
    return fixAxisInput(input);
  }

  private double fixXYInput(double input) {
    input *= JOYSTICK_SCALE;
    double JOYSTICK_DEADZONE = Constants.JOYSTICK_DEADZONE;
    if (input < JOYSTICK_DEADZONE && input > -JOYSTICK_DEADZONE) {
      return 0.0;
    }
    return input;
  }

  private double fixAxisInput(double input) {
    input *= Constants.AXIS_SCALE;
    if (input < Constants.AXIS_DEADZONE && input > -Constants.AXIS_DEADZONE) {
      return 0.0;
    }
    return input;
  }
}