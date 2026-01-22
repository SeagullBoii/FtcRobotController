package ro.mastermindsrobotics.dashboard.module.init;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.function.Supplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import ro.mastermindsrobotics.dashboard.DashboardServer;

/**
 * <strong>ModuleInitContext</strong> provides dynamic access to robot dependencies such as
 * {@link HardwareMap} and {@link Telemetry} for dashboard modules or other robot modules.
 *
 * <p>
 * Instead of passing static objects, this class uses {@link Supplier} to maintain a
 * constantly updated reference to the underlying object. This ensures that modules
 * always see the latest state, similar to pointers in C/C++.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // The context is created in {@link DashboardServer} during server initialization.
 * ModuleInitContext context = new ModuleInitContext.Builder()
 *     .setHardwareMap(() -> opModeManager.getHardwareMap())
 *     .setTelemetry(() -> opModeManager.getTelemetry())
 *     .build();
 * }</pre>
 */

public class ModuleInitContext {

    private final Supplier<HardwareMap> hardwareMap;
    private final Supplier<Telemetry> telemetry;

    private ModuleInitContext(Builder builder) {
        this.hardwareMap = builder.hardwareMap;
        this.telemetry = builder.telemetry;
    }

    public Supplier<HardwareMap> getHardwareMap() {
        return hardwareMap;
    }

    public Supplier<Telemetry> getTelemetry() {
        return telemetry;
    }

    public static class Builder {
        private Supplier<HardwareMap> hardwareMap;
        private Supplier<Telemetry> telemetry;

        /**
         * Sets the HardwareMap dependency.
         */
        public Builder setHardwareMap(Supplier<HardwareMap> hardwareMapSupplier) {
            this.hardwareMap = hardwareMapSupplier;
            return this;
        }

        /**
         * Sets the Telemetry dependency.
         */
        public Builder setTelemetry(Supplier<Telemetry> telemetrySupplier) {
            this.telemetry = telemetrySupplier;
            return this;
        }

        /**
         * Builds the immutable ModuleInitContext.
         */
        public ModuleInitContext build() {
            return new ModuleInitContext(this);
        }
    }
}
