package ro.mastermindsrobots.dashboard.module.init;

import java.util.List;

import ro.mastermindsrobots.dashboard.module.AbstractDashboardModule;
import ro.mastermindsrobots.dashboard.module.interfaces.DashboardModule;

public class ModuleUtil {
    public static void initModules(List<AbstractDashboardModule> modules, ModuleInitContext context) {
        for (DashboardModule module : modules) {
            ModuleRegistry.injectAll(module, context);
            module.readData();
        }
    }
}