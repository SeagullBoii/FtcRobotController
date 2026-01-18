package ro.mastermindsrobots.dashboard.module.core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;

import java.util.function.Supplier;

import ro.mastermindsrobots.dashboard.module.AbstractDashboardModule;
import ro.mastermindsrobots.dashboard.module.TelemetryMenu;
import ro.mastermindsrobots.dashboard.module.interfaces.TelemetryMenuUser;

public class TelemetryModule extends AbstractDashboardModule
        implements TelemetryMenuUser, OpModeManagerNotifier.Notifications {

    private Supplier<TelemetryMenu> telemetryMenuSupplier;
    private OpMode currentOpMode;

    @Override
    public String getRoute() {
        return "telemetry";
    }

    @Override
    public void setTelemetryMenu(Supplier<TelemetryMenu> telemetryMenu) {
        this.telemetryMenuSupplier = telemetryMenu;
    }

    public void attachToOpModeManager(OpModeManagerImpl opModeManager) {
        if (opModeManager != null) {
            opModeManager.registerListener(this);
        }
    }

    @Override
    public void onOpModePreInit(OpMode opMode) {
        this.currentOpMode = opMode;

        // Connect TelemetryMenu to the OpMode's telemetry
        if (telemetryMenuSupplier != null) {
            TelemetryMenu menu = telemetryMenuSupplier.get();
            if (menu != null) {
                menu.setTelemetry(() -> currentOpMode.telemetry);
            }
        }
    }

    @Override
    public void onOpModePreStart(OpMode opMode) {
    }

    @Override
    public void onOpModePostStop(OpMode opMode) {
        if (telemetryMenuSupplier != null) {
            TelemetryMenu menu = telemetryMenuSupplier.get();
            if (menu != null) {
                menu.setTelemetry(() -> null);
            }
        }
        this.currentOpMode = null;
    }

    @Override
    public Object returnData() {
        if (telemetryMenuSupplier != null) {
            TelemetryMenu menu = telemetryMenuSupplier.get();
            if (menu != null) {
                return menu.getDataForDashboard();
            }
        }
        return null;
    }
}