package gameRunners;
import org.usfirst.frc.team2791.robot.*;
import subsystems.Elevator;
import subsystems.Intake;
import config.Constants;

public class TeleopRunner {
  // refrences to the subsystems for easier access
  Elevator elevator = Robot.elevator;
  // elevator control related variables
  private boolean triggeredInc = false;
  private boolean triggeredDec = false, triggeredFall = false;
  private boolean triggeredHaveTote = false, triggeredCancel = false;
  private boolean triggeredSwapToManual = false, triggeredSwapToPreset = false, triggeredSwapToAuto = false;

  public void run() {
    Robot.mDrive.run();
    //Robot.dash.debug();
    Robot.dash.gameDisplay();
    Robot.intake.run();
    elevatorTeleop();

    Robot.compressor.start();
    Robot.dropper.run();
  }

  public void elevatorTeleop() {
    // --------- manual increase --------- //
    if (Robot.operator.getRawButton(Constants.BUTTON_RB)) {
      triggeredInc = true;
    }
    if (triggeredInc && !Robot.operator.getRawButton(Constants.BUTTON_RB)) {
      // if we're in preset mode and op presses rb, go up 1 preset
      if (elevator.isPresetMode()) {
        elevator.increasePreset();
      }

      // if we're in auto lift mode, set the next desired preset to be 1 preset higher
      if (elevator.isAutoLift()) {
        elevator.setStackHeight(elevator.getStackHeight() + 1);
      }

      //intake.retract();
      triggeredInc = false;
    }

    // --------- manual decrease --------- //
    if (Robot.operator.getRawButton(Constants.BUTTON_LB)) {
      triggeredDec = true;
    }
    if (triggeredDec && !Robot.operator.getRawButton(Constants.BUTTON_LB)) {
      if (elevator.isPresetMode()) {
        elevator.goToPreset(0);
      }

      if (elevator.isAutoLift()) {
        elevator.setStackHeight(elevator.getStackHeight() - 1);
      }

      triggeredDec = false;
    }

    // tote fell off, increase bot tote index by 1 to account
    if (Robot.operator.getRawButton(Constants.BUTTON_B)) {
      triggeredFall = true;
    }
    if (triggeredFall && !Robot.operator.getRawButton(Constants.BUTTON_B)) {
      if (elevator.isAutoLift()) {
        elevator.setNextPreset(elevator.botToteIndex + 1);
      }

      triggeredFall = false;
    }

    // --------- manual swap modes --------- //
    if (Robot.operator.getRawButton(Constants.BUTTON_LS)) {
      triggeredSwapToManual = true;
    }
    if (triggeredSwapToManual && !Robot.operator.getRawButton(Constants.BUTTON_LS)) {
      elevator.manualMode = true;
      elevator.autoLiftMode = false;
      elevator.presetMode = false;

      elevator.setOutputManual(0.0);

      triggeredSwapToManual = false;
    }

    if (Robot.operator.getRawButton(Constants.BUTTON_ST)) {
      triggeredSwapToAuto = true;
    }
    if (triggeredSwapToAuto && !Robot.operator.getRawButton(Constants.BUTTON_ST)) {
      elevator.manualMode = false;
      elevator.autoLiftMode = true;
      elevator.presetMode = false;

      elevator.goToPreset(0);

      triggeredSwapToAuto = false;
    }

    if (Robot.operator.getRawButton(Constants.BUTTON_SEL)) {
      triggeredSwapToPreset = true;
    }
    if (triggeredSwapToPreset && !Robot.operator.getRawButton(Constants.BUTTON_SEL)) {
      elevator.manualMode = false;
      elevator.autoLiftMode = false;
      elevator.presetMode = true;

      elevator.setTargetHeight(elevator.getHeight());

      for (int c = 0; c < Constants.ELEVATOR_PRESETS.length; c++) {
        if (elevator.getPresetValue(c) > elevator.getHeight()) {
          elevator.currentPresetIndex = c - 1;
          break;
        } else if (c == Constants.ELEVATOR_PRESETS.length - 1) {
          elevator.currentPresetIndex = c;
        }
      }

      triggeredSwapToPreset = false;
    }

    // if doing auto run tell the robot it's time to pickup a tote
    if (elevator.isAutoLift() && Robot.operator.getRawButton(Constants.BUTTON_A)) {
      triggeredHaveTote = true;
    }
    if (triggeredHaveTote && !Robot.operator.getRawButton(Constants.BUTTON_A)) {
      System.out.println("Manualy setting have tote to true");
      System.out.println("button A pressed");
      elevator.setToteReadyToPickup(true);
      triggeredHaveTote = false;
    }

    if (Robot.operator.getRawButton(Constants.BUTTON_RS)) {
      elevator.autoLiftReset(-1);
    }

    if (Robot.operator.getRawButton(Constants.BUTTON_X) && elevator.isAutoLift()) {
      triggeredCancel = true;
    }

    if (triggeredCancel && !Robot.operator.getRawButton(Constants.BUTTON_X)) {
      elevator.autoLiftReset(elevator.botToteIndex + 1);
      triggeredCancel = false;
    }

    // carry out the instructions given
    elevator.newRun();
  }
}
