package ro.mastermindsrobots.dashboard.module;

import ro.mastermindsrobots.dashboard.module.interfaces.DashboardModule;

public abstract class AbstractDashboardModule implements DashboardModule {
    private boolean enabled = true;

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Object returnData() {
        return null;
    }

    @Override
    public void readData() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
