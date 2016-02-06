package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IBNO055IMU;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by smonsef on 12/13/2015.
 */
@TeleOp(name="TT_Follow_Line_Test", group="8610 trials")
// @Disabled
public class TT_Follow_Line_Test extends SynchronousOpMode {
    ColorSensor colorSensor;
    DeviceInterfaceModule cdim;
    TouchSensor t;
    DcMotor motorRight;
    DcMotor motorLeft;
    LightSensor LL, LR ;
    IBNO055IMU imu;


    @Override public void main() throws InterruptedException {
        //Initialize hardware devices and their handling objects
        hardwareMap.logDevices();

        IBNO055IMU.Parameters   parameters = new IBNO055IMU.Parameters();

        parameters.angleUnit      = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelUnit      = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        parameters.loggingTag     = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

        cdim = hardwareMap.deviceInterfaceModule.get("dim");
        colorSensor = hardwareMap.colorSensor.get("mr");

        t = hardwareMap.touchSensor.get("t");

        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");

        LL = hardwareMap.lightSensor.get("light_sensor_l");
        LR = hardwareMap.lightSensor.get("light_sensor_r");

        //Instantiate ToborTech Nav object
        TT_Nav nav = new TT_Nav( motorRight, motorLeft, imu , true , LL, LR); // Not using Follow line

        nav.drive(nav.BRAKE , 0 ); // Make sure robot is stopped

        while ( opModeIsActive()) {
            while (!t.isPressed()) {
                //follow the line , using getDirection and drive methods
                int direction2go;
                direction2go = nav.getFollowLineDirection();
                nav.drive(direction2go, nav.MAX_PWR/2);
            }
            nav.drive(nav.BRAKE, 0); // Make sure robot is stopped
        }

    }
}
