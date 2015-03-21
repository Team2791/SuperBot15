package config;

public class Constants {
	
	// auton PID constnats
	public static final double AUTON_DRIVE_X_PID_OUTPUT     		= 1.0;
	public static final double AUTON_DRIVE_Y_PID_OUTPUT     		= 0.4;
	public static final double AUTON_DRIVE_ERROR_THRESHOLD			= 0.2;
	public static final double AUTON_ANGLE_ERROR_THRESHOLD			= 5.0;
	public static final double AUTON_ANGLE_START_POINT              = 45.0;
	
	// --------- drive pid --------- //
	
	public static final double GYRO_SENSITIVITY                 = 0.002; // 0.00195
	public static double       JOYSTICK_DEG_RATE                = 420.0; // not final because inherits from dashboard
	public static final double DRIVE_PID_OUTPUT                 = 0.5;
	public static final double DRIVE_PID_ERROR_THRESHOLD        = 1.5;
	public static final double DRIVE_PID_ERROR_THRESHOLD_MOVING = 5.0;
	public static final double DRIVE_PID_DISENGAGE_THRESHOLD    = 0.1;
	
	// --------- drive train --------- //
	
	public static final double  MEC_DISTANCE_PER_PULSE      = (6 * Math.PI) / 128.0;
	public static final double  FOLLOWER_DISTANCE_PER_PULSE = (4 * Math.PI) / 128.0 / 12.0;
	public static final boolean MEC_RIGHT_SIDE_REVERSED     = true;
	public static final boolean CALIBRATION_MODE            = false;
	
	// --------- joysticks --------- //
	
	public static final double JOYSTICK_DEADZONE = 0.03;
	public static final double JOYSTICK_SCALE    = 1.00;
	public static final double ELEVATOR_SCALE    = 0.70; // used on elevator teleop
	public static final double ELEVATOR_DEADZONE = 0.10; // used on elevator teleop
	public static final double AXIS_DEADZONE     = 0.05; // used for rotation
	public static final double AXIS_SCALE        = 1.00; // used for rotation
	public static final double ROTATION_RATE     = 50.0; // used for rotation
	public static final double INTAKE_DEADZONE   = 0.15;
	
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
	
	public static final int POV_TOP       = 0;
	public static final int POV_TOP_RIGHT = 45;
	public static final int POV_RIGHT     = 90;
	public static final int POV_BOT_RIGHT = 135;
	public static final int POV_BOT       = 180;
	public static final int POV_BOT_LEFT  = 225;
	public static final int POV_LEFT      = 270;
	public static final int POV_TOP_LEFT  = 315;
	public static final int POV_NONE      = -1;
	
	
	// --------- elevator pid--------- //
	
	public static final double ELEVATOR_OUTPUT_TOTE_OFFSET  = 0.08;
	public static final double ELEVATOR_PISTON_HEIGHT_SCALE = 0.70;
	
	// --------- elevator --------- //
	
	public static final double[] ELEVATOR_PRESETS       = {0.0, 6.0, 20.9, 35.85, 50.65, 65.72};
	public static final double ELEVATOR_MAX_HEIGHT     = 70.0;
	public static final double ELEVATOR_STOP_ZONE      = 0.15;
	public static final double ELEVATOR_SPEED_THRESHOLD = 3;
	public static final double ELEVATOR_DIST_PER_PULSE = 70.0 / 11.555 / 128.0; // 70.0 / 12.0 / ... for feet - also adjust presets
	public static final double ELEVATOR_MIN_SPEED      = 0.1;
	public static final double ELEVATOR_PISTON_EXTEND_THRESHOLD = 17;
	
	// auto lift conatants
	public static final double ELEVATOR_AT_TARGET_ERROR_THRESHOLD = 0.2;
	public static final double ELEVATOR_AUTO_LIFT_SETTLE_TIME = 0.5;
	public static final int ELEVATOR_STACK_HEIGHT          = 5;
	
	// --------- intake --------- //
	
	public static final double INTAKE_SPEED = 0.5;
}
