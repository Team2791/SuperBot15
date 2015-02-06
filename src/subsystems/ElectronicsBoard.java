package subsystems;

public class ElectronicsBoard {
	public int frontLeftTalon, backLeftTalon, frontRightTalon, backRightTalon;
	public int frontLeftEncoderA, backLeftEncoderA, frontRightEncoderA, backRightEncoderA;
	public int frontLeftEncoderB, backLeftEncoderB, frontRightEncoderB, backRightEncoderB;
	public int elevatorTalon, intakeTalonLeft, intakeTalonRight;
	public int elevatorEncoderA, elevatorEncoderB;
	public int elevatorLimitSwitchTop, elevatorLimitSwitchBot, totePossisionLimitSwitch;
	public int intakeSolPortLeft, intakeSolPortRight;
	public int gyro;
	public ElectronicsBoard(){
		intakeTalonLeft = 6;
		intakeTalonRight = 5;
		intakeSolPortLeft = 1; //arbitrary number till it gets set up.
		intakeSolPortRight = 2; 
		
		frontLeftTalon = 3;
		backLeftTalon = 2;
		frontRightTalon = 1;
		backRightTalon = 0; // pwm slots for talons, change before running. should be labeled...
		
		frontLeftEncoderA  = 2;
		frontLeftEncoderB  = 3;
		
		frontRightEncoderA = 0;
		frontRightEncoderB = 1;
		
		backLeftEncoderA   = 23;
		backLeftEncoderB   = 24;
		
		backRightEncoderA  = 14;
		backRightEncoderB  = 15;
		
		gyro = 1;
		
		elevatorTalon = 4;
		elevatorEncoderA = 0; //12
		elevatorEncoderB = 1; //13
		
		elevatorLimitSwitchTop = 6;
		elevatorLimitSwitchBot = 7;
		totePossisionLimitSwitch = 8;
		
	}
}
