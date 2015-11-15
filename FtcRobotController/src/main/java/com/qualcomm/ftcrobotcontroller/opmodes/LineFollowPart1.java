package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.LightSensor;



public class LineFollowPart1 extends LinearOpMode {

    DeviceInterfaceModule cdim;
    DcMotor motorRight;
    DcMotor motorLeft;
    LightSensor reflectedLightLeft, reflectedLightRight ;

    final static double LIGHT_THRESHOLD = 0.5;
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

        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        reflectedLightLeft = hardwareMap.lightSensor.get("light_sensor_l");
        reflectedLightLeft.enableLed(true);  // turn on LED of light sensor.
        reflectedLightRight = hardwareMap.lightSensor.get("light_sensor_r");
        reflectedLightRight.enableLed(true);  // turn on LED of light sensor.

        drive(BRAKE , 0 ); // Make sure robot is stopped


        waitForStart(); // waiting for start button to press

        // start motor
        drive(FORWARD,MOTOR_POWER );

        // Drive forward until you see the line
        while ( reflectedLightLeft.getLightDetected() > LIGHT_THRESHOLD ){
            drive(FORWARD,MOTOR_POWER);
            waitOneFullHardwareCycle();
        }

    }

    private int getDirection(){
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
            case 00: direction = FORWARD ;
                break;
            case 10: direction  = BRAKE ;
                break;
            case 01: direction = BRAKE;
                break;
            case 11 : direction = BRAKE ;
                break;
        }
        return direction ;
    }

    private void drive ( int direction , double power ){
        switch (direction) {
            case FORWARD :
                motorRight.setDirection(DcMotor.Direction.FORWARD);
                motorLeft.setDirection(DcMotor.Direction.REVERSE);
                motorRight.setPower(power);
                motorLeft.setPower(power);
                break;
            case BRAKE:
                motorRight.setPower(0);
                motorLeft.setPower(0);
                break;
        }
    }


}