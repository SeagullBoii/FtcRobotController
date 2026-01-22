package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import ro.mastermindsrobotics.dashboard.annotation.Configurable;

@Configurable
public class TestOpMode extends LinearOpMode {
    public static boolean testBool = false;
    private static int testInt = 10;
    public static Object obj = 14;
    private static String testStr = "test";
    @Override
    public void runOpMode() throws InterruptedException {

    }
}
