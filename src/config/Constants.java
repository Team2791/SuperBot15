package config;

public class Constants {
	public static final double JOYSTICK_DEADZONE = 0.03;
	public static final double JOYSTICK_SCALE    = 1.00;
	public static final double AXIS_SCALE        = 1.00; // used on elevator teleop
	public static final double AXIS_DEADZONE     = 0.10; // used on elevator teleop
	
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
	
	public static final int AXIS_LS    = 1;
	public static final int AXIS_RS    = 2;
	public static final int AXIS_LT    = 3;
	public static final int AXIS_RT    = 4;
	
	public static final double[] ELEVATOR_PRESETS = {1.0, 2.0, 3.0, 4.0};
	public static final double   ELEVATOR_OUTPUT_LIMIT = 0.7;
	public static final double   ELEVATOR_MAX_HEIGHT = 6.0;
	public static final double   ELEVATOR_STOP_ZONE = 0.1;
}