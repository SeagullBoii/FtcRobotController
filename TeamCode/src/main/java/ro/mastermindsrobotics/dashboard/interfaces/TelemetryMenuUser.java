package ro.mastermindsrobotics.dashboard.interfaces;

import java.util.function.Supplier;

import ro.mastermindsrobotics.dashboard.TelemetryMenu;

public interface TelemetryMenuUser {
    void setTelemetryMenu(Supplier<TelemetryMenu> telemetryMenu);
}
