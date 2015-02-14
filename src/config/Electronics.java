package config;
public class Electronics {
	
	// --------- misc --------- //
	
	public static final String CAMERA_IP = "10.27.91.11";
	public static final int    GYRO      = 1;
	
	// --------- drive train --------- //
	
	public static final int FRONT_LEFT_TALON  = 3;
	public static final int BACK_LEFT_TALON   = 2;
	public static final int FRONT_RIGHT_TALON = 1;
	public static final int BACK_RIGHT_TALON  = 0;
	
	// --------- intake --------- //
	
	public static final int INTAKE_TALON_LEFT  = 6;
	public static final int INTAKE_TALON_RIGHT = 5;
	
	public static final int INTAKE_SOLE_LEFT_REV  = 4;
	public static final int INTAKE_SOLE_LEFT_FOR  = 5;   
	public static final int INTAKE_SOLE_RIGHT_REV = 6;
	public static final int INTAKE_SOLE_RIGHT_FOR = 7;
	
	// --------- elevator --------- //
	
	public static final int ELEVATOR_TALON          = 4;
	public static final int ELEVATOR_LIM_SWITCH_TOP = 6;
	public static final int ELEVATOR_LIM_SWITCH_BOT = 7;
	public static final int TOTE_LIM_SWITCH         = 8;
	
	public static final int DROPPER_SOLE_LEFT_UP    = 0;
	public static final int DROPPER_SOLE_LEFT_DOWN  = 1;
	public static final int DROPPER_SOLE_RIGHT_UP   = 2;
	public static final int DROPPER_SOLE_RIGHT_DOWN = 3;
		
	// -------------encoders---------------------- //
	
	public static final int FRONT_LEFT_ENC_A  = 2;
	public static final int FRONT_LEFT_ENC_B  = 3;
	
	public static final int FRONT_RIGHT_ENC_A = 0;
	public static final int FRONT_RIGHT_ENC_B = 1;
	
	public static final int BACK_LEFT_ENC_A  = 23;
	public static final int BACK_LEFT_ENC_B  = 24;
	
	public static final int BACK_RIGHT_ENC_A = 14;
	public static final int BACK_RIGHT_ENC_B = 15;
	
	public static final int ELEVATOR_ENC_A   = 12;
	public static final int ELEVATOR_ENC_B   = 13;
	
	public static final int FOLLOWER_X_ENC_A = 99;
	public static final int FOLLOWER_X_ENC_B = 99;
	
	public static final int FOLLOWER_Y_ENC_A = 99;
	public static final int FOLLOWER_Y_ENC_B = 99;
}
