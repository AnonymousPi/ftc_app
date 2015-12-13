package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

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
        cdim = hardwareMap.deviceInterfaceModule.get("dim");
        colorSensor = hardwareMap.colorSensor.get("mr");

        t = hardwareMap.touchSensor.get("t");

        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");

        LL = hardwareMap.lightSensor.get("light_sensor_l");
        LR = hardwareMap.lightSensor.get("light_sensor_r");

        //Instantiate ToborTech Nav object
        TT_Nav nav = new TT_Nav( motorRight, motorLeft, imu , false, LL, LR); // Not using Follow line

        nav.drive(nav.BRAKE , 0 ); // Make sure robot is stopped

        while ( opModeIsActive()) {
            while (!t.isPressed()) {
                //follow the line , using getDirection and drive methods
                int direction2go;
                direction2go = nav.getFollowLineDirection();
                nav.drive(direction2go, nav.MIN_PWR);
            }
            nav.drive(nav.BRAKE, 0); // Make sure robot is stopped
        }

    }
}
