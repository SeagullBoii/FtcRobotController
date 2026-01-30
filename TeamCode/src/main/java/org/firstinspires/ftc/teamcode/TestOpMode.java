package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import ro.mastermindsrobotics.dashboard.TelemetryMenu;
import ro.mastermindsrobotics.dashboard.annotation.Configurable;

@Configurable
@TeleOp(name = "bsgpila", group = "group")
public class TestOpMode extends LinearOpMode {
    public static boolean testBool = false;
    private static int testInt = 10;
    public static Object obj = 14;
    private static String testStr = "test";
    DcMotorEx dcMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryMenu menu = TelemetryMenu.getInstance(telemetry);
        dcMotor = hardwareMap.get(DcMotorEx.class, "motor");
        waitForStart();
        while (opModeIsActive()) {
            menu.addLine("bagpula2");
            menu.addData("fututi mortii matii", 10);
            if (gamepad1.left_bumper)
                menu.addData("bagpula", 123);

            dcMotor.setPower(1);
            menu.addData("mot", dcMotor.getCurrent(CurrentUnit.MILLIAMPS));

            menu.update();
        }
    }
}
