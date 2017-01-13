package overriddenClasses;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShakerGyro extends SensorBase implements Runnable {
  // constants from analog devices code
  private byte ADXRS453_READ = (byte) (1 << 7);
  private byte ADXRS453_WRITE = (1 << 6);
  private SPI m_spi;
  private Timer calibrationTimer;
  private double angle = 0;
  private double rateOffset = 0;
  private double last_update_time = -1;
  private boolean recalibrate = false;
  private static final double calibrationTime = 5.0;
  private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
  private boolean calibrated = false;

  public ShakerGyro(SPI.Port port) throws InterruptedException {
    m_spi = new SPI(port);
    m_spi.setClockRate(4000000); // set to 4 MHz because that's the rRio's max, gyro can do 8 MHz
    m_spi.setMSBFirst();
    m_spi.setSampleDataOnRising();
    m_spi.setClockActiveHigh(); // set clock polarity low (yes I know it says ActiveHigh)
    m_spi.setChipSelectActiveLow();

    calibrationTimer = new Timer();
  }

  // this method keeps the gyro angle updated, run at 100hz
  public void run() {
    System.out.println("Gyro update thread started");
    recalibrate = true;
    try {
      while (true) {
        // first check if we need to run a calibrate loop
        if (recalibrate) {
          calibrated = false;
          recalibrate = false;
          calibrate();
          calibrated = true;
        } else {
          update();
        }
        // delay so loop doesn't run crazy fast
        Thread.sleep(updateDelayMs);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public double getRate() {
    byte[] data = ADXRS453_GetSensorData();
    double rate = assemble_sensor_data(data) / 80.0;
    if (rate > 301.0 || rate < -301.0) {
      System.out.println("Weird Rate: " + rate);
      System.out.println("Raw rate data: " + assemble_sensor_data(data));
      System.out.println("Hex response: " + byteArrayToHex(data));
    }
    // add in the offset calculated during calibration if it has been found
    if (calibrated) {
      rate -= rateOffset;
    }

    return rate;
  }

  public void update() {
    double fpgaTime = Timer.getFPGATimestamp();
    // ignore the first data since we don't have a time diff
    if (last_update_time != -1) {
      // get the rate of change of angle and add that to the duration of change
      double fpagTimeDiff = fpgaTime - last_update_time;
      angle += getRate() * fpagTimeDiff;
    }
    last_update_time = fpgaTime;
  }

  public double getAngle() {
    return angle;
  }

  public boolean currentlyCalibrating() {
    return calibrationTimer.get() < calibrationTime;
  }

  // tell the gyro to recalibrate in paralell to calling thread
  public void recalibrate() {
    recalibrate = true;
  }

  private void calibrate() throws InterruptedException {
    System.out.println("Gyro calibrating");
    calibrationTimer.reset();
    calibrationTimer.start();
    // reset values then wait a few seconds to accumulate some offset
    rateOffset = 0;
    reset();
    // run the gyro normally for calibrationTime
    double time_spent = calibrationTimer.get();
    while (time_spent < calibrationTime) {
      time_spent = calibrationTimer.get();
      update();
      Thread.sleep(updateDelayMs);
    }
    // find the rate offset by dividing acumulated angle by the time spend calibrating
    rateOffset = getAngle() / time_spent;
    // set the current angle to 0
    reset();
    System.out.println("Done calibrating. Rate offset = " + rateOffset);
    SmartDashboard.putNumber("Gyro rate offset", rateOffset);
  }

  private void reset() {
    angle = 0;
  }

  /**
   * @return registerValue - The sensor data.
   * @brief Reads the sensor data.
   */
  private byte[] ADXRS453_GetSensorData() {
    byte[] dataBuffer = {0, 0, 0, 0};

    byte ADXRS453_SENSOR_DATA = (1 << 5);
    dataBuffer[0] = ADXRS453_SENSOR_DATA;
    SPI_transaction(dataBuffer, dataBuffer, 4);

    return dataBuffer;
  }

  private int assemble_sensor_data(byte[] data) {
    /*
     * The data is formatted as a twos complement number with a scale factor of 80 LSB/°/sec.
		 * Therefore, the highest obtainable value for positive (clockwise) rotation is 0x7FFF (decimal +32,767),
		 * and the highest obtainable value for negative (counterclockwise) rotation is 0x8000 (decimal −32,768).
		 */
    //cast to short to make space for shifts
    //the 16 bits from the gyro are a 2's complement short
    //so we just cast it too a C++ short
    //the data is split across the output like this (MSB first): (D = data bit, X = not data)
    // X X X X X X D D | D D D D D D D D | D D D D D D X X | X X X X X X X X

    int result = 0;
    byte FIRST_BYTE_DATA = 0x3;
    result = ((int) (data[0] & FIRST_BYTE_DATA)); // remove first 6 bits keep last 2
    // shift bits 8 to the left to make room for the next byte
    result = result << 8;
    // or in bits from next byte of data
    result |= ((int) data[1]);
    // shift another 6 bits to make room for last bit of data
    result = result << 6;
    // or in bits of last data
    char THIRD_BYTE_DATA = 0xFC;
    result |= ((int) (data[2] & THIRD_BYTE_DATA)) >> 2; // remove last 2 bits keep first 6

    // check the sign bit to see if we need to invert the result
    // sign bit is the first of 16 so shifting 15 to the right will clear everything but it
    if ((result >> 15) == 1) {
      // if negative number do bitwise negate to make positive 2s compliment number
      // then set signbit again to make the number negative
      result = ~result & 0xFFFF; //negate bits and only keep last 16
      result = -result - 1;
    }

    return result;
  }

  private void SPI_transaction(byte[] inputBuffer, byte[] outputBuffer, int size) {
    check_parity(inputBuffer); // do parity bit things
    //	    System.out.println("Gyro hex command " + byteArrayToHex(inputBuffer));
    m_spi.transaction(inputBuffer, outputBuffer, 4);
    //	    System.out.println("Gyro hex response " + byteArrayToHex(outputBuffer));
  }

  void check_parity(byte[] command) {
    int num_bits = bits(command[0]) + bits(command[1]) + bits(command[2]) + bits(command[3]);
    if (num_bits % 2 == 0) {
      byte PARITY_BIT = 1;
      command[3] |= PARITY_BIT;
    }
  }

  int bits(byte val) {
    int n = 0;
    while (val != 0) {
      val &= val - 1;
      n += 1;
    }
    return n;
  }

  public static String byteArrayToHex(byte[] a) {
    StringBuilder sb = new StringBuilder(a.length * 2);
    for (byte b : a) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }
}