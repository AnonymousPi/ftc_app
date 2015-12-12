package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by Niki Monsef on 12/10/2015.
 */
public class TtColorSensor {
    ColorSensor _colorSensor;
    //DeviceInterfaceModule cdim;
    //TouchSensor t;

    float _currentRed  ;
    float _currentBlue ;

    float[] _runningRed  = new float[10] ;
    float[] _runningBlue = new float[10] ;

    TtColorSensor(ColorSensor colorSensor){
        _colorSensor = colorSensor ;
        for ( int i = 0 ; i < 10 ; i++){
            _runningRed[i] = 0 ;
            _runningBlue[i]= 0 ;
        }
    }

    static  int count = 0;
    float red_acc = 0 , blue_acc = 0 , red_final = 0 , blue_final = 0;

    int getColor(){
        insertNewSamples( _colorSensor.red(), _colorSensor.blue());
        calcFinal();
        if ( _currentBlue > ( _currentRed + 2) ){
            return  1; // 1 = Blue
        }
        else if ( _currentRed > ( _currentBlue + 2 )){
            return 2 ; // 2 = Red
        }
        else {
            return 0 ; // not sure ( not enough difference )
        }
    }

    private void insertNewSamples(float red, float blue){
        for ( int i = 0 ; i < 10 ; i++ ) {
            _runningRed[i+1]=_runningRed[i] ; // 0->1 ; 1->2 ; ... ; 10-> drop
            _runningRed[i+1]=_runningRed[i] ; // 0->1 ; 1->2 ; ... ; 10-> drop
        }
        _runningRed[0]  = red ;
        _runningBlue[0] = blue ;
    }

    private void calcFinal(){
        float sumOfRed  = 0 ;
        float sumOfBlue = 0 ;
        for ( int i = 0 ; i < 10 ; i++ ){
            sumOfBlue += _runningBlue[i] ;
            sumOfRed  += _runningRed[i]  ;
        }
        _currentRed  = sumOfRed ;
        _currentBlue = sumOfBlue ;
    }

}
