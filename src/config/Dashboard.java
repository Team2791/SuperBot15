package config;
import org.usfirst.frc.team2791.robot.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Dashboard {
  Preferences pref;

  public Dashboard() {
    pref = Preferences.getInstance();
  }

  public void debug() {
    debugJoysticks();
    debugEncoders();
    debugDrive();
    debugDrivePID();
    debugElevator();
    debugDropper();
    debugAuton();
    Robot.elevator.display();
  }

  public void gameDisplay() {
    // drive system
    double[] wheelSpeeds = Robot.mDrive.getWheelSpeeds();
    SmartDashboard.putString("Wheel Speeeds",
      String.format("FL: %f, FR %f, BL: %f, BR: %f", wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]));

    // elevator
    SmartDashboard.putNumber("Elevator Height", Robot.elevator.getHeight());
    SmartDashboard.putNumber("Elevator preset", Robot.elevator.getPresetIndex());
    SmartDashboard.putNumber("Elevator setpoint", Robot.elevator.getPresetIndex());

    SmartDashboard.putString("Elevator Piston", Robot.elevator.getPistonState());
    SmartDashboard.putBoolean("Elevator at top", Robot.elevator.atTop());
    SmartDashboard.putBoolean("Elevator at bottom", Robot.elevator.atBot());
    SmartDashboard.putBoolean("Elevator encoder calibrated", Robot.elevator.getEncoderCalibrated());

    Robot.elevator.display();

    // dropper
    SmartDashboard.putString("Drop state", Robot.dropper.getState());
  }

  public void debugJoysticks() {
    SmartDashboard.putNumber("Driver X", Robot.driver.getx());
    SmartDashboard.putNumber("Driver Y", Robot.driver.gety());
    SmartDashboard.putNumber("Spin", Robot.mDrive.getSpin());
  }

  public void debugEncoders() {
    SmartDashboard.putNumber("Dist from origin", Robot.encoders.getRealDistance());

    SmartDashboard.putNumber("X Distance", Robot.encoders.encoderX.getDistance());
    SmartDashboard.putNumber("Y Distance", Robot.encoders.encoderY.getDistance());
  }

  public void debugDrive() {
    double[] wheelSpeeds = Robot.mDrive.getWheelSpeeds();
    SmartDashboard.putString("Wheel Speeeds",
      String.format("FL: %f, FR %f, BL: %f, BR: %f", wheelSpeeds[0], wheelSpeeds[1], wheelSpeeds[2], wheelSpeeds[3]));

    SmartDashboard.putString("Drive Type", Robot.mDrive.getDriveType());
  }

  public void debugDrivePID() {
    SmartDashboard.putNumber("Gyro PID output", Robot.mDrive.getPIDOutput());
    SmartDashboard.putNumber("Gyro Target", Robot.mDrive.getTargetAngle());
  }

  public void debugElevator() {
    SmartDashboard.putNumber("Elevator Height", Robot.elevator.getHeight());
    SmartDashboard.putNumber("Elevator preset", Robot.elevator.getPresetIndex());
    SmartDashboard.putNumber("Elevator setpoint", Robot.elevator.getPresetIndex());

    SmartDashboard.putString("Elevator Piston", Robot.elevator.getPistonState());
    SmartDashboard.putBoolean("Elevator at top", Robot.elevator.atTop());
    SmartDashboard.putBoolean("Elevator at bottom", Robot.elevator.atBot());
    SmartDashboard.putBoolean("Elevator encoder calibrated", Robot.elevator.getEncoderCalibrated());
  }

  public void debugDropper() {
    SmartDashboard.putString("Drop state", Robot.dropper.getState());
  }

  public void debugAuton() {
    SmartDashboard.putNumber("Auton State", Robot.autonRunner.getState());
    SmartDashboard.putNumber("AutoY error", Robot.autonRunner.driverY.getError());
    SmartDashboard.putNumber("AutoX error", Robot.autonRunner.driverX.getError());
    SmartDashboard.putNumber("AutoSp error", Robot.autonRunner.driverSpin.getError());

    SmartDashboard.putNumber("AutoY set", Robot.autonRunner.driverY.getSetpoint());
    SmartDashboard.putNumber("AutoX set", Robot.autonRunner.driverX.getSetpoint());
    SmartDashboard.putNumber("AutonSp set", Robot.autonRunner.driverSpin.getSetpoint());
    SmartDashboard.putNumber("ErrorTimer", Robot.autonRunner.errorTimer.get());
    SmartDashboard.putNumber("WaitTimer", Robot.autonRunner.waitTimer.get());
  }

  public int getIntFix(String key, int def) {
    int val = pref.getInt(key, def);
    if (val == def) {
      pref.putInt(key, val);
    }
    return val;
  }

  public double getDoubleFix(String key, double def) {
    double val = pref.getDouble(key, def);
    if (val == def) {
      pref.putDouble(key, val);
    }
    return val;
  }
}
