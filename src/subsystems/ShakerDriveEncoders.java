package subsystems;
import config.Constants;
import config.Electronics;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;

public class ShakerDriveEncoders {
	public Encoder encoderFL, encoderBL, encoderFR, encoderBR;
	public Encoder encoderX, encoderY;
	
	public ShakerDriveEncoders(){
		encoderFL = new Encoder(Electronics.FRONT_LEFT_ENC_A ,Electronics.FRONT_LEFT_ENC_B, !Constants.MEC_RIGHT_SIDE_REVERSED, CounterBase.EncodingType.k4X);
		encoderBL = new Encoder(Electronics.BACK_LEFT_ENC_A, Electronics.BACK_LEFT_ENC_B, !Constants.MEC_RIGHT_SIDE_REVERSED, CounterBase.EncodingType.k4X);
		encoderFR = new Encoder(Electronics.FRONT_RIGHT_ENC_A, Electronics.FRONT_RIGHT_ENC_B, Constants.MEC_RIGHT_SIDE_REVERSED, CounterBase.EncodingType.k4X);
		encoderBR = new Encoder(Electronics.BACK_RIGHT_ENC_A, Electronics.BACK_RIGHT_ENC_B, Constants.MEC_RIGHT_SIDE_REVERSED, CounterBase.EncodingType.k4X);
		
		encoderFL.setDistancePerPulse(Constants.MEC_DISTANCE_PER_PULSE);
		encoderBL.setDistancePerPulse(Constants.MEC_DISTANCE_PER_PULSE);
		encoderFR.setDistancePerPulse(Constants.MEC_DISTANCE_PER_PULSE);
		encoderBR.setDistancePerPulse(Constants.MEC_DISTANCE_PER_PULSE);
		
		// encoderX = new Encoder(Electronics.FOLLOWER_X_ENC_A, Electronics.FOLLOWER_X_ENC_B, false, CounterBase.EncodingType.k4X);
		//encoderY = new Encoder(Electronics.FOLLOWER_Y_ENC_A, Electronics.FOLLOWER_Y_ENC_B, false, CounterBase.EncodingType.k4X);
		
		//encoderX.setDistancePerPulse(Constants.FOLLOWER_DISTANCE_PER_PULSE);
		//encoderY.setDistancePerPulse(Constants.FOLLOWER_DISTANCE_PER_PULSE);
	}
	
	public void resetAll(){
		encoderFL.reset();
		encoderFR.reset();
		encoderBL.reset();
		encoderBR.reset();
		
		//encoderX.reset();
		//encoderY.reset();
	}
	
	public double getRealDistance(){
		/*double resultFront = 0, resultBack = 0;
		
		resultFront = Math.pow(Robot.encoders.encoderFL.getDistance(),2) +  Math.pow(Robot.encoders.encoderFR.getDistance(),2);
		resultFront = Math.sqrt(resultFront);
		
		resultBack = Math.pow(Robot.encoders.encoderBL.getDistance(),2) +  Math.pow(Robot.encoders.encoderBR.getDistance(),2);
		resultBack = Math.sqrt(resultBack);
		
		if(resultFront != resultBack){ return -123456.0; }
		else{ return resultFront; }*/
		
		double x = encoderX.getDistance();
		double y = encoderY.getDistance();
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
}
