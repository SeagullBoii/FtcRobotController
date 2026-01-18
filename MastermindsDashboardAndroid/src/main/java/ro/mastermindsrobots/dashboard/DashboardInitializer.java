package ro.mastermindsrobots.dashboard;

import static ro.mastermindsrobots.dashboard.DashboardServer.DEFAULT_PORT;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import java.lang.reflect.Field;

public class DashboardInitializer {
    private static final String TAG = "DashboardInitializer";
    private static final int MAX_RETRIES = 100;
    private static final int RETRY_DELAY_MS = 200;

    @OnCreateEventLoop
    public static void initDashboard(Context context, EventLoop eventLoop) {
        Log.i(TAG, "Starting Dashboard server...");

        DashboardServer server = DashboardServer.getInstance(context);

        try {
            Field field = eventLoop.getClass().getDeclaredField("opModeManager");
            field.setAccessible(true);
            OpModeManagerImpl opModeManager = (OpModeManagerImpl) field.get(eventLoop);

            if (opModeManager != null) {
                server.setOpModeManager(opModeManager);
                Log.i(TAG, "OpModeManager set successfully");
            } else {
                Log.w(TAG, "OpModeManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get OpModeManager from EventLoop", e);
        }

        try {
            server.startServer();
            Log.i(TAG, "Dashboard server started on port " + DEFAULT_PORT);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start Dashboard server", e);
            throw new RuntimeException(e);
        }

        pollForOpModesReady(server, 0);
    }

    private static void pollForOpModesReady(DashboardServer server, int attempt) {
        if (attempt >= MAX_RETRIES) {
            Log.e(TAG, "Failed to initialize OpModes after " + MAX_RETRIES + " attempts");
            return;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                RegisteredOpModes registeredOpModes = RegisteredOpModes.getInstance();

                Field opmodesRegisteredField = RegisteredOpModes.class.getDeclaredField("opmodesAreRegistered");
                opmodesRegisteredField.setAccessible(true);
                boolean isRegistered = opmodesRegisteredField.getBoolean(registeredOpModes);

                if (!isRegistered) {
                    Log.d(TAG, "OpModes not registered yet, retry " + (attempt + 1) + "/" + MAX_RETRIES);
                    pollForOpModesReady(server, attempt + 1);
                    return;
                }

                Field opModesLockField = RegisteredOpModes.class.getDeclaredField("opModesLock");
                opModesLockField.setAccessible(true);
                Object lock = opModesLockField.get(registeredOpModes);

                synchronized (lock) {
                    Log.i(TAG, "OpModes registered and locked, initializing controller");
                    server.initOpModeController();
                    Log.i(TAG, "Dashboard fully initialized");
                }

            } catch (NoSuchFieldException e) {
                Log.e(TAG, "Reflection failed - SDK structure may have changed", e);
            } catch (Exception e) {
                Log.d(TAG, "OpModes not ready yet, retry " + (attempt + 1) + "/" + MAX_RETRIES + ": " + e.getMessage());
                pollForOpModesReady(server, attempt + 1);
            }
        }, RETRY_DELAY_MS);
    }
}