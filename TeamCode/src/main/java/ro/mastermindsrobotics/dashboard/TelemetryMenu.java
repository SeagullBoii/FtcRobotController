package ro.mastermindsrobotics.dashboard;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TelemetryMenu {
    private Supplier<Telemetry> telemetrySupplier;
    private Map<String, Object> dataMap = new LinkedHashMap<>();

    public void setTelemetry(Supplier<Telemetry> telemetry) {
        this.telemetrySupplier = telemetry;
    }

    public void addData(String caption, Object value) {
        dataMap.put(caption, value);

        if (telemetrySupplier != null) {
            Telemetry telemetry = telemetrySupplier.get();
            if (telemetry != null) {
                telemetry.addData(caption, value);
            }
        }
    }

    public void update() {
        if (telemetrySupplier != null) {
            Telemetry telemetry = telemetrySupplier.get();
            if (telemetry != null)
                telemetry.update();
        }
    }

    public void clear() {
        dataMap.clear();
        if (telemetrySupplier != null) {
            Telemetry telemetry = telemetrySupplier.get();
            if (telemetry != null)
                telemetry.clear();
        }
    }

    public Map<String, Object> getDataForDashboard() {
        return new LinkedHashMap<>(dataMap);
    }
}