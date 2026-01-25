    package ro.mastermindsrobotics.dashboard;

    import android.content.Context;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;

    import com.google.gson.Gson;
    import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
    import com.qualcomm.robotcore.hardware.VoltageSensor;

    import java.io.IOException;
    import java.io.InputStream;
    import java.util.List;

    import fi.iki.elonen.NanoHTTPD;
    import ro.mastermindsrobotics.dashboard.module.core.ConfigurableVariablesModule;
    import ro.mastermindsrobotics.dashboard.module.core.LatencyModule;
    import ro.mastermindsrobotics.dashboard.module.core.TelemetryModule;
    import ro.mastermindsrobotics.dashboard.module.init.ModuleInitContext;
    import ro.mastermindsrobotics.dashboard.module.init.ModuleUtil;
    import ro.mastermindsrobotics.dashboard.module.core.BatteryVoltageModule;
    import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;

    public class DashboardServer extends NanoHTTPD {
        public static final int DEFAULT_PORT = 21050;
        private static final String TAG = "DashboardServer";
        private static final Gson gson = new Gson();

        private static DashboardServer instance;

        private final Context appContext;
        private OpModeManagerImpl opModeManager;
        private Handler mainHandler = new Handler(Looper.getMainLooper());
        private List<AbstractDashboardModule> modules = List.of(new BatteryVoltageModule(), new ConfigurableVariablesModule(), new TelemetryModule(), new LatencyModule());

        private DashboardOpModeController opModeController;

        private boolean initialized = false;

        private DashboardServer(int port, Context context) {
            super(port);
            this.appContext = context.getApplicationContext();
        }

        public static synchronized DashboardServer getInstance(Context context) {
            if (instance == null) {
                if (context == null)
                    throw new IllegalStateException("DashboardServer not initialized yet; provide a valid context first");
                instance = new DashboardServer(DEFAULT_PORT, context);
            }
            return instance;
        }

        public void startServer() {
            try {
                if (!this.isAlive()) {
                    this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    android.util.Log.i("DashboardServer", "Server started on port " + getListeningPort());
                }
            } catch (Exception e) {
                android.util.Log.e("DashboardServer", "Failed to start server", e);
                throw new RuntimeException(e);
            }
            initializeIfReady();
        }

        public void initOpModeController() {
            if (opModeController == null && opModeManager != null) {
                opModeController = new DashboardOpModeController(opModeManager);
                Log.i(TAG, "OpModeController initialized successfully");
            }
        }

        public synchronized void initializeIfReady() {
            if (initialized) return;

            if (opModeManager == null) return;
            if (modules == null || modules.isEmpty()) return;

            onServerInitialized();
            initialized = true;

            Log.i(TAG, "DashboardServer fully initialized");
        }

        private void onServerInitialized() {
            initOpModeController();

            ModuleUtil.initModules(modules,
                    new ModuleInitContext.Builder()
                            .setHardwareMap(() -> opModeManager.getHardwareMap())
                            .build());

            for (AbstractDashboardModule module: modules)
                module.init();
        }

        private Response serveAsset(String fileName, String mimeType) {
            if (appContext == null) {
                return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Context not available");
            }
            try {
                InputStream stream = appContext.getAssets().open(fileName);
                return NanoHTTPD.newChunkedResponse(Response.Status.OK, mimeType, stream);
            } catch (IOException e) {
                Log.e(TAG, "Failed to serve asset: " + fileName, e);
                return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Asset not found");
            }
        }

        private Response serveApiRoute(String uri, IHTTPSession session) {
            String route = uri.substring("/api/".length());

            for (AbstractDashboardModule module : modules) {
                if (!module.isEnabled())
                    continue;

                if (module.getRoute().equals(route)) {
                    try {
                        module.onRequest(session);
                        Object data = module.returnData();
                        String json = gson.toJson(data);

                        return NanoHTTPD.newFixedLengthResponse(
                                Response.Status.OK,
                                "application/json",
                                json
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to serve API route: " + route, e);
                        return NanoHTTPD.newFixedLengthResponse(
                                Response.Status.INTERNAL_ERROR,
                                "text/plain",
                                "Failed to retrieve module data"
                        );
                    }
                }
            }

            return NanoHTTPD.newFixedLengthResponse(
                    Response.Status.NOT_FOUND,
                    "text/plain",
                    "Unknown API route"
            );
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            if (uri.startsWith("/api/"))
                return serveApiRoute(uri, session);

            if (uri.startsWith("/images/"))
                return serveAsset("dashboard" + uri, "image/png");

            switch (uri) {
                case "/":
                case "/index.html":
                    double voltage = Double.POSITIVE_INFINITY;
                    for (VoltageSensor sensor : opModeManager.getHardwareMap().voltageSensor) {
                        voltage = Math.min(voltage, sensor.getVoltage());
                    }
                    return serveAsset("dashboard/index.html", "text/html");

                case "/style.css":
                    return serveAsset("dashboard/style.css", "text/css");

                case "/script.js":
                    return serveAsset("dashboard/script.js", "application/javascript");

                default:
                    Log.w(TAG, "Unknown route: " + uri);
                    return NanoHTTPD.newFixedLengthResponse(
                            Response.Status.NOT_FOUND,
                            "text/plain",
                            "404 Not Found"
                    );
            }
        }

        public Context getContext() {
            return appContext;
        }

        public void setOpModeManager(OpModeManagerImpl manager) {
            this.opModeManager = manager;
            initializeIfReady();
        }

        public OpModeManagerImpl getOpModeManager() {
            return opModeManager;
        }
    }
