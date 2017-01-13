package config;
public class Electronics {

  // --------- misc --------- //
  public static final int GYRO = 1;

  // --------- drive train --------- //
  public static final int FRONT_LEFT_TALON = 0; //3
  public static final int BACK_LEFT_TALON = 1; //2
  public static final int FRONT_RIGHT_TALON = 3; //1
  public static final int BACK_RIGHT_TALON = 2; //0

  // --------- intake --------- //
  public static final int INTAKE_TALON_LEFT = 6;
  public static final int INTAKE_TALON_RIGHT = 5;
  public static final int INTAKE_SOLE_LEFT_REV = 4;
  public static final int INTAKE_SOLE_LEFT_FOR = 5;
  public static final int INTAKE_SOLE_RIGHT_REV = 6;
  public static final int INTAKE_SOLE_RIGHT_FOR = 7;

  // --------- elevator --------- //
  public static final int ELEVATOR_TALON = 4;
  public static final int ELEVATOR_LIM_SWITCH_TOP = 3;
  public static final int ELEVATOR_LIM_SWITCH_BOT = 2;
  public static final int TOTE_LIM_SWITCH = 8;
  public static final int ELEVATOR_PISTON_FOR = 3;
  public static final int ELEVATOR_PISTON_REV = 2;
  public static final int DROPPER_PISTON_UP = 1;
  public static final int DROPPER_PISTON_DOWN = 0;

  // -------------encoders---------------------- //
  public static final int ELEVATOR_ENC_A = 0;
  public static final int ELEVATOR_ENC_B = 1;
  public static final int FOLLOWER_X_ENC_A = 4;
  public static final int FOLLOWER_X_ENC_B = 5;
  public static final int FOLLOWER_Y_ENC_A = 6;
  public static final int FOLLOWER_Y_ENC_B = 7;
}
