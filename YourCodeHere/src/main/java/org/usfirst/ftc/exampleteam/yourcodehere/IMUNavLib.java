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

    //final static double MOTOR_POWER = 0.2;// Higher values will cause the robot to move faster
    final static double MIN_PWR = 0.15 ;
    final static double MAX_PWR = 0.99 ;
    final static double ERR_MRGN = 0.05 ;

    final static double P = 0.0045;
    double cntAcc;

    private DcMotor _motorLeft ;
    private DcMotor _motorRight ;
    private IBNO055IMU _imu ;

    // The setter functions
    public void set_motorRight ( DcMotor motor) {
            _motorRight = motor;
        }
    public void set_motorLeft ( DcMotor motor) {
        _motorLeft = motor;
    }
    public void set_imu (IBNO055IMU imu ){
        _imu = imu ;
    }


    public void drive (int direction , double power ){

        switch (direction) {
            case FORWARD :
                _motorRight.setDirection(DcMotor.Direction.FORWARD);
                _motorLeft.setDirection(DcMotor.Direction.REVERSE);
                _motorRight.setPower(power);
                _motorLeft.setPower(power);
                break;
            case BACKWARD:
                _motorRight.setDirection(DcMotor.Direction.REVERSE);
                _motorLeft.setDirection(DcMotor.Direction.FORWARD);
                _motorRight.setPower(power);
                _motorLeft.setPower(power);
                break;
            case LEFT:
                _motorRight.setPower(0);
                _motorLeft.setPower(power);
                break;
            case RIGHT:
                _motorRight.setPower(power);
                _motorLeft.setPower(0);
                break;
            case BRAKE:
                _motorRight.setPower(0);
                _motorLeft.setPower(0);
                break;
        }
    }


    void adjustAngle(double offsetFromTarget){

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

        _motorRight.setPower(rightPower);
        _motorLeft.setPower(leftPower);
    }


    //returns 0 if the heading == TargetHeading
    int setHeading(double TargetHeading){
        double currentHeading ;
        double theta ;
        int returnVal = 0 ;

        currentHeading = _imu.getAngularOrientation().heading;
        theta = (TargetHeading - currentHeading) % 360;
        // mess with margin of error
        returnVal = 1 ;
        if ( Math.abs(theta) > ERR_MRGN ){
            adjustAngle(theta);
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

    void moveTo(double heading) {
        while (setHeading(heading)== 1){
        }
    }

}
