package ro.mastermindsrobotics.dashboard;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import ro.mastermindsrobots.dashboard.annotation.Configurable;

@Configurable
@TeleOp(name="TestOpMode", group = "DashboardExamples")
public class TestOpMode extends LinearOpMode {

    public static int testVar = 15;
    DcMotorEx motor1;

    @Override
    public void runOpMode() throws InterruptedException {
        while (opModeIsActive()) {
        }
    }
}
