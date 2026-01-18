package ro.mastermindsrobots.dashboard.module.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import ro.mastermindsrobots.dashboard.module.interfaces.DashboardModule;
import ro.mastermindsrobots.dashboard.module.interfaces.HardwareMapUser;

public class ModuleRegistry {
    private static final Map<Class<?>, BiConsumer<DashboardModule, ModuleInitContext>> registry = new HashMap<>();

    static {
        registry.put(HardwareMapUser.class,
                (module, ctx) -> ((HardwareMapUser) module).setHardwareMap(ctx.getHardwareMap()));
    }

    public static void injectAll(DashboardModule module, ModuleInitContext ctx) {
        for (Map.Entry<Class<?>, BiConsumer<DashboardModule, ModuleInitContext>> entry : registry.entrySet()) {
            if (entry.getKey().isInstance(module))
                entry.getValue().accept(module, ctx);
        }
    }
}
