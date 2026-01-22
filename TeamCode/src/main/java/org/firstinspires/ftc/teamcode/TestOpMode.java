package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import ro.mastermindsrobotics.dashboard.TelemetryMenu;
import ro.mastermindsrobotics.dashboard.annotation.Configurable;

@Configurable
@TeleOp(name = "bsgpila", group = "group")
public class TestOpMode extends LinearOpMode {
    public static boolean testBool = false;
    private static int testInt = 10;
    public static Object obj = 14;
    private static String testStr = "test";

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryMenu menu = TelemetryMenu.getInstance(telemetry);
        waitForStart();
        while (opModeIsActive()) {
            menu.addLine("bagpula2");
            menu.addData("fututi mortii matii", 10);
            if (gamepad1.left_bumper)
                menu.addData("bagpula", 123);
            menu.update();
        }
    }
}
