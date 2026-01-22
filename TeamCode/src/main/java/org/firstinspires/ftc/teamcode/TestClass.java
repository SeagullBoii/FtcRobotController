package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import ro.mastermindsrobotics.dashboard.TelemetryMenu;
import ro.mastermindsrobotics.dashboard.annotation.Configurable;

@Configurable
@TeleOp(name = "bsgpila2", group = "group")public class TestClass extends LinearOpMode {
    public static int testNum1 = 15;
    public static double testNum2 = 20;


    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addLine("fmm");
            telemetry.update();
        }
    }
}
