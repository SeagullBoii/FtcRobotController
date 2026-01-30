package ro.mastermindsrobotics.dashboard.interfaces;

import fi.iki.elonen.NanoHTTPD;

/**
 * Base interface for all dashboard modules.
 *
 * <p>A {@code DashboardModule} represents a self-contained unit of data
 * exposed by the dashboard backend. Each module owns a unique API route
 * and is responsible for handling incoming requests and publishing data
 * to the frontend.</p>
 *
 * <p>Typical lifecycle:
 * <ol>
 *   <li>Module instance is created</li>
 *   <li>Dependencies are injected during server initialization</li>
 *   <li>{@link #init()} is called once when the server is ready</li>
 *   <li>{@link #onRequest(NanoHTTPD.IHTTPSession)} is invoked for each
 *       incoming API request targeting this module</li>
 *   <li>{@link #returnData()} is called to serialize and publish the
 *       module's latest data</li>
 * </ol>
 * </p>
 */
public interface DashboardModule {

    /**
     * Returns a unique identifier for this module.
     *
     * <p>The default implementation typically returns the simple class name,
     * but implementations may override this if a stable or custom identifier
     * is required.</p>
     *
     * @return a unique module identifier
     */
    String getId();

    /**
     * Returns the API route associated with this module.
     *
     * <p>This route is appended to {@code /api/} and must be unique across
     * all registered modules. The frontend uses this route to send requests
     * and retrieve data for the module.</p>
     *
     * @return the backend API route for this module
     */
    String getRoute();

    /**
     * Returns the most recently produced data for this module.
     *
     * <p>This method should be side-effect free and must not perform
     * hardware access or request parsing. Any request-dependent logic
     * should be handled in {@link #onRequest(NanoHTTPD.IHTTPSession)}.</p>
     *
     * @return an object to be serialized and sent to the frontend,
     *         or {@code null} if no data is available
     */
    Object returnData();

    /**
     * Handles an incoming HTTP request for this module.
     *
     * <p>This method is invoked by the server whenever an API request
     * targeting this module's route is received. Implementations may
     * read query parameters, headers, or request bodies and update
     * internal state accordingly.</p>
     *
     * <p>This method should return quickly and must not block.</p>
     *
     * @param session the HTTP request session associated with the API call
     * @return
     */
    NanoHTTPD.Response onRequest(NanoHTTPD.IHTTPSession session);

    /**
     * Called once after the server has finished initializing and all
     * dependencies have been injected.
     *
     * <p>Modules should perform any required setup here.</p>
     */
    void init();

    /**
     * Enables or disables this module.
     *
     * <p>Disabled modules should avoid handling requests and publishing data.</p>
     *
     * @param enabled whether the module should be enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Indicates whether this module is currently enabled.
     *
     * @return {@code true} if the module is enabled
     */
    boolean isEnabled();
}
