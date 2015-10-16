package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class ColorSensor01 extends LinearOpMode {

    ColorSensor colorSensor;
    DeviceInterfaceModule cdim;
    TouchSensor t;

    @Override
    public void runOpMode() throws InterruptedException {
        hardwareMap.logDevices();

        cdim = hardwareMap.deviceInterfaceModule.get("dim");
        colorSensor = hardwareMap.colorSensor.get("mr");

        t = hardwareMap.touchSensor.get("t");

        waitForStart();

        int count = 0;
        float red_acc = 0 , blue_acc = 0 , red_final = 0 , blue_final = 0;
        while (opModeIsActive()) {

            count++;
            red_acc += colorSensor.red();
            blue_acc += colorSensor.blue();

            if (count == 10){
                red_final = red_acc;
                blue_final = blue_acc;
                red_acc = 0;
                blue_acc = 0;
                count = 0;
                telemetry.addData("Red", red_final);
                telemetry.addData("Blue", blue_final);
            }


        }
    }

}
