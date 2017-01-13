package subsystems;
import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;

public class ShakerDriveEncoders {
  public Encoder encoderX, encoderY;

  public ShakerDriveEncoders() {
    encoderX = new Encoder(Electronics.FOLLOWER_X_ENC_A, Electronics.FOLLOWER_X_ENC_B, false,
      CounterBase.EncodingType.k4X);
    encoderY = new Encoder(Electronics.FOLLOWER_Y_ENC_A, Electronics.FOLLOWER_Y_ENC_B, false,
      CounterBase.EncodingType.k4X);

    encoderX.setDistancePerPulse(Constants.FOLLOWER_DISTANCE_PER_PULSE);
    encoderY.setDistancePerPulse(Constants.FOLLOWER_DISTANCE_PER_PULSE);

    encoderX.stopLiveWindowMode();
    encoderY.stopLiveWindowMode();
  }

  public void resetAll() {
    encoderX.reset();
    encoderY.reset();
  }

  public double getRealDistance() {
    double x = encoderX.getDistance();
    double y = encoderY.getDistance();
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }
}
