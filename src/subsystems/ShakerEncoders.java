package subsystems;

import org.usfirst.frc.team2791.robot.Robot;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;

public class ShakerEncoders {

	public boolean RIGHT_REVERSED;
	public Encoder encoderFL, encoderBL, encoderFR, encoderBR;
	public double DISTANCE_PER_PULSE;
	
	public ShakerEncoders(){
		DISTANCE_PER_PULSE = (6 * Math.PI) / 128;
		RIGHT_REVERSED = true;
		
		encoderFL = new Encoder(Robot.eBoard.frontLeftEncoderA, Robot.eBoard.frontLeftEncoderB, !RIGHT_REVERSED, CounterBase.EncodingType.k4X);
		encoderBL = new Encoder(Robot.eBoard.backLeftEncoderA, Robot.eBoard.backLeftEncoderB, !RIGHT_REVERSED, CounterBase.EncodingType.k4X);
		encoderFR = new Encoder(Robot.eBoard.frontRightEncoderA, Robot.eBoard.frontRightEncoderB, RIGHT_REVERSED, CounterBase.EncodingType.k4X);
		encoderBR = new Encoder(Robot.eBoard.backRightEncoderA, Robot.eBoard.backRightEncoderB, RIGHT_REVERSED, CounterBase.EncodingType.k4X);
		
		encoderFL.setDistancePerPulse(DISTANCE_PER_PULSE);
		encoderBL.setDistancePerPulse(DISTANCE_PER_PULSE);
		encoderFR.setDistancePerPulse(DISTANCE_PER_PULSE);
		encoderBR.setDistancePerPulse(DISTANCE_PER_PULSE);
	}
	
	public void resetAll(){
		encoderFL.reset();
		encoderFR.reset();
		encoderBL.reset();
		encoderBR.reset();
	}
	
	public double getRealDistance(){
		double resultFront = 0, resultBack = 0;
		
		resultFront = Math.pow(Robot.encoders.encoderFL.getDistance(),2) +  Math.pow(Robot.encoders.encoderFR.getDistance(),2);
		resultFront = Math.sqrt(resultFront);
		
		resultBack = Math.pow(Robot.encoders.encoderBL.getDistance(),2) +  Math.pow(Robot.encoders.encoderBR.getDistance(),2);
		resultBack = Math.sqrt(resultBack);
		
		if(resultFront != resultBack){ return -123456.0; }
		else{ return resultFront; }
	}
}
