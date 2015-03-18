package overriddenClasses;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;

public class ShakerDrive extends RobotDrive implements MotorSafety{

	public ShakerDrive(SpeedController frontLeftMotor,  SpeedController rearLeftMotor,
					   SpeedController frontRightMotor, SpeedController rearRightMotor){
		
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}
	
	public static class MotorType {

        public final int value;
        static final int kFrontLeft_val = 0;
        static final int kFrontRight_val = 1;
        static final int kRearLeft_val = 2;
        static final int kRearRight_val = 3;
        
        public static final MotorType kFrontLeft = new MotorType(kFrontLeft_val);
        public static final MotorType kFrontRight = new MotorType(kFrontRight_val);
        public static final MotorType kRearLeft = new MotorType(kRearLeft_val);
        public static final MotorType kRearRight = new MotorType(kRearRight_val);

        private MotorType(int value) {
            this.value = value;
        }
    }
	
	public static double[] rotate(double x, double y, double gyroAngle){
		return rotateVector(x, y, gyroAngle);
	}
	
    public double[] mecanumDrive_Cartesian_report(double x, double y, double rotation, double gyroAngle) {
        if(!kMecanumCartesian_Reported) {
            UsageReporting.report(tResourceType.kResourceType_RobotDrive, getNumMotors(), tInstances.kRobotDrive_MecanumCartesian);
            kMecanumCartesian_Reported = true;
        }
        double xIn = x;
        double yIn = y;
        // Negate y for the joystick.
        yIn = -yIn;
        // Compenstate for gyro angle.
        double rotated[] = rotateVector(xIn, yIn, gyroAngle);
        xIn = rotated[0];
        yIn = rotated[1];

        double wheelSpeeds[] = new double[kMaxNumberOfMotors];
        wheelSpeeds[MotorType.kFrontLeft_val] = xIn + yIn + rotation;
        wheelSpeeds[MotorType.kFrontRight_val] = -xIn + yIn - rotation;
        wheelSpeeds[MotorType.kRearLeft_val] = -xIn + yIn + rotation;
        wheelSpeeds[MotorType.kRearRight_val] = xIn + yIn - rotation;

        normalize(wheelSpeeds);
        m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft_val] * m_invertedMotors[MotorType.kFrontLeft_val] * m_maxOutput, m_syncGroup);
        m_frontRightMotor.set(wheelSpeeds[MotorType.kFrontRight_val] * m_invertedMotors[MotorType.kFrontRight_val] * m_maxOutput, m_syncGroup);
        m_rearLeftMotor.set(wheelSpeeds[MotorType.kRearLeft_val] * m_invertedMotors[MotorType.kRearLeft_val] * m_maxOutput, m_syncGroup);
        m_rearRightMotor.set(wheelSpeeds[MotorType.kRearRight_val] * m_invertedMotors[MotorType.kRearRight_val] * m_maxOutput, m_syncGroup);

        if (m_syncGroup != 0) {
            CANJaguar.updateSyncGroup(m_syncGroup);
        }

        if (m_safetyHelper != null) m_safetyHelper.feed();
        
        return wheelSpeeds;
    }
	
	public double[] getMotorOutputs(double magnitude, double direction, double rotation){
		
		double[] speeds = new double[4];
		
		if(!kMecanumPolar_Reported) {
            UsageReporting.report(tResourceType.kResourceType_RobotDrive, getNumMotors(), tInstances.kRobotDrive_MecanumPolar);
            kMecanumPolar_Reported = true;
        }
        // Normalized for full power along the Cartesian axes.
        magnitude = limit(magnitude) * Math.sqrt(2.0);
        // The rollers are at 45 degree angles.
        double dirInRad = (direction + 45.0) * 3.14159 / 180.0;
        double cosD = Math.cos(dirInRad);
        double sinD = Math.sin(dirInRad);

        double wheelSpeeds[] = new double[kMaxNumberOfMotors];
        wheelSpeeds[MotorType.kFrontLeft_val] = (sinD * magnitude + rotation);
        wheelSpeeds[MotorType.kFrontRight_val] = (cosD * magnitude - rotation);
        wheelSpeeds[MotorType.kRearLeft_val] = (cosD * magnitude + rotation);
        wheelSpeeds[MotorType.kRearRight_val] = (sinD * magnitude - rotation);

        normalize(wheelSpeeds);
        
        speeds[0] = wheelSpeeds[MotorType.kFrontLeft_val] * m_invertedMotors[MotorType.kFrontLeft_val] * m_maxOutput;
        speeds[1] = wheelSpeeds[MotorType.kFrontRight_val] * m_invertedMotors[MotorType.kFrontRight_val] * m_maxOutput;
        speeds[2] = wheelSpeeds[MotorType.kRearLeft_val] * m_invertedMotors[MotorType.kRearLeft_val] * m_maxOutput;
        speeds[3] = wheelSpeeds[MotorType.kRearRight_val] * m_invertedMotors[MotorType.kRearRight_val] * m_maxOutput;
        
        return speeds;        
	}
}
