package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class SensorTest_1 extends LinearOpMode {

    ColorSensor colorSensor;
    DeviceInterfaceModule cdim;
    TouchSensor t;
    DcMotor motorRight;
    DcMotor motorLeft;
    LightSensor reflectedLightLeft, reflectedLightRight ;

    final static double LIGHT_THRESHOLD = 0.4;
    final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster

    // Defining constants for drive direction
    final static int FORWARD    = 0 ;
    final static int BACKWARD   = 1 ;
    final static int LEFT       = 2 ;
    final static int RIGHT      = 3 ;
    final static int BRAKE      = 4 ;



    @Override
    public void runOpMode() throws InterruptedException {

        hardwareMap.logDevices();

        cdim = hardwareMap.deviceInterfaceModule.get("dim");

        colorSensor = hardwareMap.colorSensor.get("mr");
        t = hardwareMap.touchSensor.get("t");

        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");

        reflectedLightLeft = hardwareMap.lightSensor.get("light_sensor_l");
        reflectedLightLeft.enableLed(true);  // turn on LED of light sensor.
        reflectedLightRight = hardwareMap.lightSensor.get("light_sensor_r");
        reflectedLightRight.enableLed(true);  // turn on LED of light sensor.

        drive(BRAKE , 0 ); // Make sure robot is stopped


        waitForStart(); // waiting for start button to press

        // start motor
        drive(FORWARD,MOTOR_POWER );

        // Drive forward until you hit the touch sensor
        while ( !t.isPressed() ){
            //follow the line , using getDirection and drive methods
            int direction2go ;
            direction2go = getDirection ();
            drive(direction2go,MOTOR_POWER);
            waitOneFullHardwareCycle();
        }

        //stop motor
        drive(BRAKE , 0 );

        int count = 0;
        float red_acc = 0 , blue_acc = 0 , red_final = 0 , blue_final = 0;

            /*
            //This loop measures and returns color data
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
            */

        //measure and accumulate sensor samples 10 times
        for ( count = 0 ; count < 10; count++) {
            red_acc += colorSensor.red();
            blue_acc += colorSensor.blue();
        }
        red_final = red_acc;
        blue_final = blue_acc;
        telemetry.addData("Red", red_final);
        telemetry.addData("Blue", blue_final);

        // call servo function to press the right button here <<<<<<
    }


    // this method reads the light sensors and returns which direction to move
    private int getDirection(){
        double random = Math.random(); // user this to flip a coin
        int direction = FORWARD ;
        int left_on  = 0 ;
        int right_on = 0 ;
        int dir = 0 ;

        if ( reflectedLightLeft.getLightDetected() > LIGHT_THRESHOLD ){
            left_on = 1 ;
        }
        if ( reflectedLightRight.getLightDetected() > LIGHT_THRESHOLD ){
            right_on = 1 ;
        }

        dir = left_on * 10 + right_on  ; // gives 0,1,10,11

        switch (dir){
            case 11: direction = FORWARD ;
                break;
            case 01: direction  = LEFT ;
                break;
            case 10: direction = RIGHT;
                break;
            case 00 :
                if ( random < 0.50 ){
                    direction = LEFT ;
                }
                else{
                    direction = RIGHT ;
                }
                break;
        }
        return direction ;
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