    package ro.mastermindsrobotics.dashboard;

    import org.firstinspires.ftc.robotcore.external.Telemetry;

    import java.util.ArrayList;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.function.Supplier;

    public class TelemetryMenu {
        private Supplier<Telemetry> telemetrySupplier;
        private static Map<String, TelemetryEntry> entries = new LinkedHashMap<>();
        private static TelemetryMenu instance;
        private static Map<String, TelemetryEntry> currentEntries = new LinkedHashMap<>();
        private static Map<String, TelemetryEntry> displayEntries = new LinkedHashMap<>();
        private static AtomicInteger lineCounter = new AtomicInteger(0);

        public static TelemetryMenu getInstance(Telemetry telemetry) {
            if (instance == null)
                instance = telemetry != null ? new TelemetryMenu(telemetry) : new TelemetryMenu();
            return instance;
        }

        private TelemetryMenu(Telemetry telemetry) {
            this.setTelemetry(() -> telemetry);
        }

        private TelemetryMenu() {
        }

        public void setTelemetry(Supplier<Telemetry> telemetry) {
            this.telemetrySupplier = telemetry;
        }


        public void addData(String caption, Object value) {
            Object safeValue = (value == null) ? "null" : value.toString();
            currentEntries.put(caption, new TelemetryEntry(caption, safeValue, false));

            Telemetry telemetry = telemetrySupplier != null ? telemetrySupplier.get() : null;
            if (telemetry != null)
                telemetry.addData(caption, value);
        }

        public DualTelemetryLine addLine() {
            return addLine("");
        }

        public DualTelemetryLine addLine(String caption) {
            String lineKey = "__line__" + lineCounter.getAndIncrement();
            currentEntries.put(lineKey, new TelemetryEntry(lineKey, caption, true));

            Telemetry telemetry = telemetrySupplier != null ? telemetrySupplier.get() : null;
            Telemetry.Line telemetryLine = telemetry != null ? telemetry.addLine(caption) : null;

            return new DualTelemetryLine(telemetryLine, new DashboardLine(lineKey));
        }

        public void update() {
            synchronized (displayEntries) {
                displayEntries.clear();
                displayEntries.putAll(currentEntries);
                currentEntries.clear();
                lineCounter.set(0);
            }

            Telemetry telemetry = telemetrySupplier != null ? telemetrySupplier.get() : null;
            if (telemetry != null)
                telemetry.update();
        }


        public static Map<String, TelemetryEntry> getLatestData() {
            synchronized (displayEntries) {
                return new LinkedHashMap<>(displayEntries);
            }
        }
        public static void clearLatestData() {
            entries.clear();
            lineCounter.set(0);
        }

        public void clear() {
            entries.clear();
            lineCounter.set(0);

            if (telemetrySupplier != null) {
                Telemetry telemetry = telemetrySupplier.get();
                if (telemetry != null)
                    telemetry.clear();
            }
        }

        public static class TelemetryEntry {
            public final String caption;
            public final Object value;
            public final boolean isLine;

            public TelemetryEntry(String caption, Object value, boolean isLine) {
                this.caption = caption;
                this.value = value;
                this.isLine = isLine;
            }
        }

        public class DashboardLine {
            private final String lineKey;

            DashboardLine(String lineKey) {
                this.lineKey = lineKey;
            }

            public DashboardLine addData(String caption, Object value) {
                String fullCaption = lineKey + "." + caption;
                entries.put(fullCaption, new TelemetryEntry(caption, value, false));
                return this;
            }
        }

        public class DualTelemetryLine {
            public final Telemetry.Line telemetryLine;
            public final DashboardLine dashboardLine;

            DualTelemetryLine(Telemetry.Line telemetryLine, DashboardLine dashboardLine) {
                this.telemetryLine = telemetryLine;
                this.dashboardLine = dashboardLine;
            }

            public DualTelemetryLine addData(String caption, Object value) {
                dashboardLine.addData(caption, value);
                if (telemetryLine != null)
                    telemetryLine.addData(caption, value);
                return this;
            }

            public DualTelemetryLine addData(String caption, String format, Object... args) {
                dashboardLine.addData(caption, String.format(format, args));
                if (telemetryLine != null)
                    telemetryLine.addData(caption, format, args);
                return this;
            }
        }
    }