package ro.mastermindsrobotics.dashboard.interfaces;

/**
 * Base interface for all dashboard modules.
 *
 * <p>A {@code DashboardModule} represents a unit of data that can be
 * periodically read from the robot and exposed to the dashboard backend.</p>
 *
 * <p>Typical lifecycle:
 * <ol>
 *   <li>Module is created</li>
 *   <li>Dependencies are injected (e.g. HardwareMap) at the end of server init</li>
 *   <li>{@link #readData()} is called periodically</li>
 *   <li>{@link #returnData()} is called to publish data</li>
 * </ol>
 * </p>
 */
public interface DashboardModule {

    /**
     * @return a unique identifier for this module
     */
    String getId();

    /**
     * Returns the backend route used to send and retrieve this module's data.
     *
     * <p>This route must be unique across all modules and should be stable,
     * as it is used by the dashboard to associate incoming data.</p>
     *
     * @return the backend API route for this module
     */
    String getRoute();

    /**
     * Returns the most recently read data for this module.
     *
     * <p>This method should <b>not</b> perform hardware access.
     * Hardware polling should be done in {@link #readData()}.</p>
     *
     * @return the data object to be sent to the dashboard, or {@code null}
     *         if no data is available
     */
    Object returnData();

    /**
     * Reads data from hardware or other sources and caches it internally.
     *
     * <p>This method is typically called periodically (e.g. every loop cycle).</p>
     */
    void readData();


    /**
     * <p>This method is called on server init.</p>
     */
    void init();

    /**
     * Enables or disables this module.
     *
     * <p>Disabled modules should avoid reading hardware or publishing data.</p>
     *
     * @param enabled whether the module should be enabled
     */
    void setEnabled(boolean enabled);

    /**
     * @return {@code true} if the module is currently enabled
     */
    boolean isEnabled();
}
