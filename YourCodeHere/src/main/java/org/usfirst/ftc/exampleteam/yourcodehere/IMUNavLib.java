package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.swerverobotics.library.interfaces.IBNO055IMU;

public class IMUNavLib {

    // Defining constants for drive direction
    final static int FORWARD    = 0 ;
    final static int BACKWARD   = 1 ;
    final static int LEFT       = 2 ;
    final static int RIGHT      = 3 ;
    final static int BRAKE      = 4 ;

    final static double MOTOR_POWER = 0.2;// Higher values will cause the robot to move faster
    final static double MIN_PWR = 0.15 ;
    final static double MAX_PWR = 0.99 ;
    final static double ERR_MRGN = 0.05 ;

    final static double P = 0.0045;
    double cntAcc;

    private DcMotor _motorRight ;

        public void set_motorRight ( DcMotor motor) {
            _motorRight = motor;
        }


    private DcMotor _motorLeft ;

    public void set_motorLeft ( DcMotor motor) {
        _motorLeft = motor;
    }


    private void drive (DcMotor motorRight, DcMotor motorLeft, int direction , double power ){

        switch (direction) {
            case FORWARD :
                motorRight.setDirection(DcMotor.Direction.FORWARD);
                motorLeft.setDirection(DcMotor.Direction.REVERSE);
                motorRight.setPower(power);
                motorLeft.setPower(power);
                break;
            case BACKWARD:
                motorRight.setDirection(DcMotor.Direction.REVERSE);
                motorLeft.setDirection(DcMotor.Direction.FORWARD);
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


    void adjustAngle(DcMotor motorRight, DcMotor motorLeft, double offsetFromTarget){

        double rightPower, leftPower ;

        rightPower =  offsetFromTarget * P;
        // set variables for max and min power
        if (Math.abs(rightPower)< MIN_PWR){
            if ( rightPower < 0 ) {
                rightPower = -MIN_PWR;
            }
            else{
                rightPower = MIN_PWR ;
            }
        }
        if (Math.abs(rightPower)> MAX_PWR){
            if ( rightPower < 0 ) {
                rightPower = -MAX_PWR;
            }
            else{
                rightPower = MAX_PWR ;
            }
        }
        leftPower  = - rightPower;

        motorRight.setPower(rightPower);
        motorLeft.setPower(leftPower);
    }


    //returns 0 if the heading == TargetHeading
    int setHeading(DcMotor motorRight, DcMotor motorLeft, IBNO055IMU myImu, double TargetHeading){
        double currentHeading ;
        double theta ;
        int returnVal = 0 ;

        currentHeading = myImu.getAngularOrientation().heading;
        theta = (TargetHeading - currentHeading) % 360;
        // mess with margin of error
        returnVal = 1 ;
        if ( Math.abs(theta) > ERR_MRGN ){
            adjustAngle(motorRight, motorLeft, theta);
        }
        else {
            cntAcc ++ ;
            if (cntAcc > 50){
                returnVal = 0 ;
                cntAcc = 0 ;
            }
        }

        return returnVal ;
    }

    void moveTo(DcMotor motorRight, DcMotor motorLeft, IBNO055IMU myImu, double heading) {
        while (setHeading(motorRight, motorLeft, myImu, heading)== 1){
        }


    }









}
