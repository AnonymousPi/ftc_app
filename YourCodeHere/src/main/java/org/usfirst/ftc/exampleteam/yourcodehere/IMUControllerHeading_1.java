package org.usfirst.ftc.exampleteam.yourcodehere;

        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.util.ElapsedTime;

        import org.swerverobotics.library.ClassFactory;
        import org.swerverobotics.library.SynchronousOpMode;
        import org.swerverobotics.library.interfaces.EulerAngles;
        import org.swerverobotics.library.interfaces.IBNO055IMU;
        import org.swerverobotics.library.interfaces.IFunc;
        import org.swerverobotics.library.interfaces.II2cDeviceClientUser;
        import org.swerverobotics.library.interfaces.Position;
        import org.swerverobotics.library.interfaces.TeleOp;
        import org.swerverobotics.library.interfaces.Velocity;

/**
 * SynchIMUDemo gives a short demo on how to use the BNO055 Inertial Motion Unit (IMU) from AdaFruit.
 * http://www.adafruit.com/products/2472
 */
@TeleOp(name="IMU Control Heading_1", group="8610 trials")
// @Disabled
public class IMUControllerHeading_1 extends SynchronousOpMode
{

    DcMotor motorRight;
    DcMotor motorLeft;
    final static double MOTOR_POWER = 0.2; // Higher values will cause the robot to move faster

    // Defining constants for drive direction
    final static int FORWARD    = 0 ;
    final static int BACKWARD   = 1 ;
    final static int LEFT       = 2 ;
    final static int RIGHT      = 3 ;
    final static int BRAKE      = 4 ;

    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    // Our sensors, motors, and other devices go here, along with other long term state
    IBNO055IMU imu;
    ElapsedTime elapsed    = new ElapsedTime();
    IBNO055IMU.Parameters   parameters = new IBNO055IMU.Parameters();
    IMUNavLib imuNav = new IMUNavLib();

    // Here we have state we use for updating the dashboard. The first of these is important
    // to read only once per update, as its acquisition is expensive. The remainder, though,
    // could probably be read once per item, at only a small loss in display accuracy.
    int                     loopCycles;
    int                     i2cCycles;
    double                  ms;

    double initialHeading;
    double theta;
    double targetHeading;
    double forwardPower;
    final static double P = 0.01;

    double leftPower;
    double rightPower;

    double currentAngle, targetAngle;
    double currentTime;
    double startTime;
    int i ;

    //----------------------------------------------------------------------------------------------
    // main() loop
    //----------------------------------------------------------------------------------------------

    @Override public void main() throws InterruptedException
    {
        // We are expecting the IMU to be attached to an I2C port on  a core device interface
        // module and named "imu". Retrieve that raw I2cDevice and then wrap it in an object that
        // semantically understands this particular kind of sensor.
        parameters.angleunit      = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelunit      = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        parameters.loggingTag     = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");

        imuNav.set_motorRight(motorRight) ;
        imuNav.set_motorLeft (motorLeft) ;
        imuNav.set_imu(imu);

        imuNav.drive(BRAKE, 0); // Make sure robot is stopped

        // Wait until we're told to go
        waitForStart();
        initialHeading = imu.getAngularOrientation().heading ;
        targetHeading = initialHeading;
        forwardPower = MOTOR_POWER ;

        //targetAngle = targetHeading + 90 ;
        startTime = elapsed.time() * 1000.0;
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        boolean done = false ;
        while ( opModeIsActive() ){
            if ( !done) {
                targetAngle = (targetHeading + 90) % 360;
                imuNav.moveTo(targetAngle);
                imuNav.drive(BRAKE, 0);
                Thread.sleep(1000);
                targetAngle = (targetAngle + 90) % 360;
                Thread.sleep(1000);
                imuNav.drive(BRAKE, 0);
                done = true ;
            }
            idle();
        }
        imuNav.drive(BRAKE, MOTOR_POWER);
    }

 }















