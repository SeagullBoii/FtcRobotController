package ro.mastermindsrobotics.dashboard.module.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ro.mastermindsrobotics.dashboard.interfaces.DashboardModule;
import ro.mastermindsrobotics.dashboard.interfaces.HardwareMapUser;
import ro.mastermindsrobotics.dashboard.interfaces.TelemetryMenuUser;
import ro.mastermindsrobotics.dashboard.module.core.TelemetryModule;

public class ModuleRegistry {
    private static final Map<Class<?>, BiConsumer<DashboardModule, ModuleInitContext>> registry = new HashMap<>();

    static {
        registry.put(HardwareMapUser.class,
                (module, ctx) -> ((HardwareMapUser) module).setHardwareMap(ctx.getHardwareMap()));
        registry.put(TelemetryMenuUser.class,
                (module, ctx) ->
                        ((TelemetryModule) module).setTelemetryMenu(ctx.getTelemetryMenu()));
    }

    public static void injectAll(DashboardModule module, ModuleInitContext ctx) {
        for (Map.Entry<Class<?>, BiConsumer<DashboardModule, ModuleInitContext>> entry : registry.entrySet()) {
            if (entry.getKey().isInstance(module))
                entry.getValue().accept(module, ctx);
        }
    }
}
