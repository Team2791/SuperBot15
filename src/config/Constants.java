package config;

public class Constants {
	
	// --------- drive pid --------- //
	
	public static final double GYRO_SENSITIVITY                 = 0.002; // 0.00195
	public static double       JOYSTICK_DEG_RATE                = 420.0; // not final because inherits from dashboard
	public static final double DRIVE_PID_OUTPUT                 = 0.3;
	public static final double DRIVE_PID_ERROR_THRESHOLD        = 1.5;
	public static final double DRIVE_PID_ERROR_THRESHOLD_MOVING = 5.0;
	
	// --------- drive train --------- //
	
	public static final double  MEC_DISTANCE_PER_PULSE   = (6 * Math.PI) / 128.0;
	public static final boolean MEC_RIGHT_SIDE_REVERSED  = true;
	public static final boolean CALIBRATION_MODE         = false;
	
	// --------- joysticks --------- //
	
	public static final double JOYSTICK_DEADZONE = 0.03;
	public static final double JOYSTICK_SCALE    = 1.00;
	public static final double ELEVATOR_SCALE    = 1.00; // used on elevator teleop
	public static final double ELEVATOR_DEADZONE = 0.10; // used on elevator teleop
	public static final double AXIS_DEADZONE     = 0.05; // used for rotation
	public static final double AXIS_SCALE        = 1.00; // used for rotation
	public static final double ROTATION_RATE     = 50.0; // used for rotation
	
	public static final int BUTTON_A   = 1;
	public static final int BUTTON_B   = 2;
	public static final int BUTTON_X   = 3;
	public static final int BUTTON_Y   = 4;
	public static final int BUTTON_LB  = 5;
	public static final int BUTTON_RB  = 6;
	public static final int BUTTON_SEL = 7;
	public static final int BUTTON_ST  = 8;
	public static final int BUTTON_LS  = 9;
	public static final int BUTTON_RS  = 10;
	
	public static final int AXIS_LS_X  = 0;
	public static final int AXIS_LS_Y  = 1;
	public static final int AXIS_LT    = 2;
	public static final int AXIS_RT    = 3;
	public static final int AXIS_RS_X  = 4;
	public static final int AXIS_RS_Y  = 5;
	
	// --------- elevator pid--------- //
	
	public static final double ELEVATOR_OUTPUT_TOTE_OFFSET = 0.08;
	
	// --------- elevator --------- //
	
	public static final double[] ELEVATOR_PRESETS        = {1.0, 2.0, 3.0, 4.0};
	public static final double   ELEVATOR_OUTPUT_LIMIT   = 0.7;
	public static final double   ELEVATOR_MAX_HEIGHT     = 6.0;
	public static final double   ELEVATOR_STOP_ZONE      = 0.1;
	public static final double   ELEVATOR_DIST_PER_PULSE = (11.0018 / 12.0) / 128.0; // in feet
	
	// --------- intake --------- //
	
	public static final double INTAKE_SPEED = 0.5;
}
