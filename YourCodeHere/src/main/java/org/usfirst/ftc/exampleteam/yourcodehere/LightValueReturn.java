package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.LightSensor;

import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by noshadrahimi on 10/20/15.
 */
@TeleOp(name="LIGHT VALUE RETURN")
public class LightValueReturn extends LinearOpMode {

    LightSensor reflectedLightLeft, reflectedLightRight;

    @Override
    public void runOpMode() throws InterruptedException {


        reflectedLightLeft = hardwareMap.lightSensor.get("light_sensor_l");
        reflectedLightLeft.enableLed(true);  // turn on LED of light sensor.
        reflectedLightRight = hardwareMap.lightSensor.get("light_sensor_r");
        reflectedLightRight.enableLed(true);  // turn on LED of light sensor.

        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("Right", reflectedLightRight.getLightDetected());
            telemetry.addData("Left", reflectedLightLeft.getLightDetected());


        }

    }
}
