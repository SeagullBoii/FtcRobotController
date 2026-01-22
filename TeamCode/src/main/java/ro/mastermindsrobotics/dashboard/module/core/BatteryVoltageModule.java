package ro.mastermindsrobotics.dashboard.module.core;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.Map;
import java.util.function.Supplier;

import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;
import ro.mastermindsrobotics.dashboard.interfaces.HardwareMapUser;

public class BatteryVoltageModule extends AbstractDashboardModule implements HardwareMapUser {
    private Supplier<HardwareMap> hardwareMap;

    @Override
    public String getRoute() {
        return "batteryVoltage";
    }

    @Override
    public Object returnData() {
        double voltage = getBatteryVoltage();
        return Map.of("voltage", voltage);
    }

    private double getBatteryVoltage() {
        if (hardwareMap == null) return -1.0;

        double minVoltage = Double.POSITIVE_INFINITY;

        for (VoltageSensor sensor : hardwareMap.get().voltageSensor) {
            double voltage = sensor.getVoltage();

            if (voltage > 0 && voltage < minVoltage) {
                minVoltage = voltage;
            }
        }

        return minVoltage == Double.POSITIVE_INFINITY ? -1.0 : minVoltage;
    }

    @Override
    public void setHardwareMap(Supplier<HardwareMap> map) {
        Log.i("BatteryVoltageModule", "HardwareMap injected: " + (map != null));
        this.hardwareMap = map;
    }
}
