package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Created by Niki Monsef on 12/13/2015.
 */
@TeleOp(name="TT_ColorSensor_Test", group="8610 trials")
// @Disabled
public class TT_ColorSensor_Test extends SynchronousOpMode {
    ColorSensor colorSensor;

    @Override public void main() throws InterruptedException
    {
        colorSensor = hardwareMap.colorSensor.get("mr");
        TT_ColorPicker colorPicker = new TT_ColorPicker(colorSensor) ;

        while ( opModeIsActive()) {
            telemetry.addData("red", this.colorSensor.red());
            telemetry.addData("green", this.colorSensor.green());
            telemetry.addData("TTColorPicker Value", colorPicker.getColor());
        }
    }

}
