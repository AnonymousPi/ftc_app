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

    // Here we have state we use for updating the dashboard. The first of these is important
    // to read only once per update, as its acquisition is expensive. The remainder, though,
    // could probably be read once per item, at only a small loss in display accuracy.
    EulerAngles angles;
    Position position;
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

        drive(BRAKE, 0); // Make sure robot is stopped

        // Enable reporting of position using the naive integrator
        //imu.startAccelerationIntegration(new Position(), new Velocity());

        // Set up our dashboard computations
        composeDashboard();

        // Wait until we're told to go
        waitForStart();

        angles = imu.getAngularOrientation();
        initialHeading = angles.heading ;

        targetHeading = initialHeading;
        forwardPower = MOTOR_POWER ;

        //targetAngle = targetHeading + 90 ;
        startTime = elapsed.time() * 1000.0;
        telemetry.update();
        //i = 1 ;
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        while ( opModeIsActive() ){
            //currentTime = elapsed.time() * 1000.0;
            //telemetry.update();
            targetAngle = (targetHeading +  90)% 360 ;
            while (setHeading(targetAngle)== 1){
                telemetry.update();
                idle();
            }
            drive(BRAKE, MOTOR_POWER);



            idle();
        }

        drive(BRAKE, MOTOR_POWER);

    }

    void adjustAngle(double offsetFromTarget){
        rightPower =  offsetFromTarget * P;
        // set variables for max and min power
        if (Math.abs(rightPower)<0.07){
            if ( rightPower < 0 ) {
                rightPower = -0.07;
            }
            else{
                rightPower = 0.07 ;
            }
        }
        if (Math.abs(rightPower)> 0.99){
            if ( rightPower < 0 ) {
                rightPower = -0.99;
            }
            else{
                rightPower = 0.99 ;
            }
        }
        leftPower  = - rightPower;

        motorRight.setPower(rightPower);
        motorLeft.setPower(leftPower);

    }

    //returns 0 if the heading == TargetHeading
    int setHeading(double TargetHeading){
        double currentHeading ;
        double Theta ;
        int returnVal = 0 ;
        //angles = imu.getAngularOrientation();
        //currentAngle = angles.heading;
        i++ ;
        currentHeading = imu.getAngularOrientation().heading;
        Theta = (TargetHeading - currentHeading) % 360;
        currentAngle = currentHeading ;
        theta = Theta ;

        if ( Math.abs(Theta) > 1 ){
            adjustAngle(Theta);
            returnVal = 1 ;
        }

        return returnVal ;
    }


    //----------------------------------------------------------------------------------------------
    // dashboard configuration
    //----------------------------------------------------------------------------------------------

    void composeDashboard()
    {
        // The default dashboard update rate is a little to slow for us, so we update faster
        telemetry.setUpdateIntervalMs(100);

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            //angles     = imu.getAngularOrientation();
            //position   = imu.getPosition();

            // The rest of this is pretty cheap to acquire, but we may as well do it
            // all while we're gathering the above.
            loopCycles = getLoopCount();
            i2cCycles  = ((II2cDeviceClientUser) imu).getI2cDeviceClient().getI2cCycleCount();
            ms         = elapsed.time() * 1000.0;
        }
        });
        telemetry.addLine(
                telemetry.item("I value: ", new IFunc<Object>() {
                    public Object value() {
                        return i;
                    }
                }));
        telemetry.addLine(
                telemetry.item("Theta value: ", new IFunc<Object>() {
                    public Object value() {
                        return theta;
                    }
                }));
        telemetry.addLine(
                telemetry.item("Current Angle: ", new IFunc<Object>() {
                    public Object value() {
                        return currentAngle;
                    }
                }));
        telemetry.addLine(
                telemetry.item("Target Angle: ", new IFunc<Object>() {
                    public Object value() {
                        return targetAngle;
                    }
                }));
        telemetry.addLine(
                telemetry.item("Right Power: ", new IFunc<Object>() {
                    public Object value() {
                        return rightPower;
                    }
                }));
        telemetry.addLine(
                telemetry.item("Left Power: ", new IFunc<Object>() {
                    public Object value() {
                        return leftPower;
                    }
                }));
        telemetry.addLine(
                telemetry.item("loop count: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return loopCycles;
                    }
                }),
                telemetry.item("i2c cycle count: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return i2cCycles;
                    }
                }));

        telemetry.addLine(
                telemetry.item("loop rate: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatRate(ms / loopCycles);
                    }
                }),
                telemetry.item("i2c cycle rate: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatRate(ms / i2cCycles);
                    }
                }));


        telemetry.addLine(
                telemetry.item("heading: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.heading);
                    }
                }),
                telemetry.item("roll: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.roll);
                    }
                }),
                telemetry.item("pitch: ", new IFunc<Object>()
                {
                    public Object value()
                    {
                        return formatAngle(angles.pitch);
                    }
                }));

    }

    String formatAngle(double angle)
    {
        return parameters.angleunit==IBNO055IMU.ANGLEUNIT.DEGREES ? formatDegrees(angle) : formatRadians(angle);
    }
    String formatRadians(double radians)
    {
        return formatDegrees(degreesFromRadians(radians));
    }
    String formatDegrees(double degrees)
    {
        return String.format("%.1f", normalizeDegrees(degrees));
    }
    String formatRate(double cyclesPerSecond)
    {
        return String.format("%.2f", cyclesPerSecond);
    }
    String formatPosition(double coordinate)
    {
        String unit = parameters.accelunit== IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC
                ? "m" : "??";
        return String.format("%.2f%s", coordinate, unit);
    }

    //----------------------------------------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------------------------------------

    /** Normalize the angle into the range [-180,180) */
    double normalizeDegrees(double degrees)
    {
        while (degrees >= 180.0) degrees -= 360.0;
        while (degrees < -180.0) degrees += 360.0;
        return degrees;
    }
    double degreesFromRadians(double radians)
    {
        return radians * 180.0 / Math.PI;
    }

    /** Turn a system status into something that's reasonable to show in telemetry */
    String decodeStatus(int status)
    {
        switch (status)
        {
            case 0: return "idle";
            case 1: return "syserr";
            case 2: return "periph";
            case 3: return "sysinit";
            case 4: return "selftest";
            case 5: return "fusion";
            case 6: return "running";
        }
        return "unk";
    }

    /** Turn a calibration code into something that is reasonable to show in telemetry */
    String decodeCalibration(int status)
    {
        StringBuilder result = new StringBuilder();

        result.append(String.format("s%d", (status >> 2) & 0x03));  // SYS calibration status
        result.append(" ");
        result.append(String.format("g%d", (status >> 2) & 0x03));  // GYR calibration status
        result.append(" ");
        result.append(String.format("a%d", (status >> 2) & 0x03));  // ACC calibration status
        result.append(" ");
        result.append(String.format("m%d", (status >> 0) & 0x03));  // MAG calibration status

        return result.toString();
    }

    private void drive ( int direction , double power ){
        switch (direction) {
            case FORWARD :
                motorRight.setDirection(DcMotor.Direction.REVERSE);
                motorLeft.setDirection(DcMotor.Direction.FORWARD);
                motorRight.setPower(power);
                motorLeft.setPower(power);
                break;
            case BACKWARD:
                motorRight.setDirection(DcMotor.Direction.FORWARD);
                motorLeft.setDirection(DcMotor.Direction.REVERSE);
                motorRight.setPower(power);
                motorLeft.setPower(power);
                break;
            case LEFT:
                motorRight.setPower(0);
                motorLeft.setPower(power);
                break;
            case RIGHT:
                motorRight.setPower(power);
                motorLeft.setPower(0);
                break;
            case BRAKE:
                motorRight.setPower(0);
                motorLeft.setPower(0);
                break;
        }
    }
}
