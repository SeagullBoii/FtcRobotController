package ro.mastermindsrobotics.dashboard.module.init;

import java.util.List;

import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;
import ro.mastermindsrobotics.dashboard.interfaces.DashboardModule;

public class ModuleUtil {
    public static void initModules(List<AbstractDashboardModule> modules, ModuleInitContext context) {
        for (DashboardModule module : modules)
            ModuleRegistry.injectAll(module, context);
    }
}