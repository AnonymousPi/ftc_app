package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.IBNO055IMU;

/**
 * Created by noshadrahimi on 2/5/16.
 */
public class LinearIMUTest extends LinearOpMode {

    IBNO055IMU imu;
    IBNO055IMU.Parameters   parameters = new IBNO055IMU.Parameters();

    public void runOpMode() throws InterruptedException {

        parameters.angleUnit      = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelUnit      = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        parameters.loggingTag     = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);


        waitForStart();

        while (opModeIsActive()){

            telemetry.addData("Heading:", imu.getAngularOrientation().heading);

        }

    }

}
