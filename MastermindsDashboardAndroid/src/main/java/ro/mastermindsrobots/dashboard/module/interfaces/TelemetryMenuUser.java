package ro.mastermindsrobots.dashboard.module.interfaces;

import java.util.function.Supplier;

import ro.mastermindsrobots.dashboard.module.TelemetryMenu;

public interface TelemetryMenuUser {
    void setTelemetryMenu(Supplier<TelemetryMenu> telemetryMenu);
}
