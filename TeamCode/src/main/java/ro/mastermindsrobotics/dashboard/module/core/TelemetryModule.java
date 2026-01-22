    package ro.mastermindsrobotics.dashboard.module.core;

    import com.qualcomm.robotcore.eventloop.opmode.OpMode;
    import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
    import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;

    import java.util.LinkedHashMap;
    import java.util.Map;
    import java.util.function.Supplier;

    import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;
    import ro.mastermindsrobotics.dashboard.TelemetryMenu;
    import ro.mastermindsrobotics.dashboard.interfaces.TelemetryMenuUser;

    public class TelemetryModule extends AbstractDashboardModule
            implements TelemetryMenuUser, OpModeManagerNotifier.Notifications {

        private Supplier<TelemetryMenu> telemetryMenuSupplier;
        private OpMode currentOpMode;
        private Map<String, Object> lastSnapshotData = new LinkedHashMap<>();

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
            this.lastSnapshotData.clear();
        }

        @Override
        public Object returnData() {
            return TelemetryMenu.getLatestData();

        }
    }