package ro.mastermindsrobots.dashboard;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DashboardOpModeController {

    private static final String TAG = "DashboardOpModeController";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final OpModeManagerImpl opModeManager;
    private final RegisteredOpModes registeredOpModes;

    private Map<String, OpModeMeta> opModeMap = new HashMap<>();

    public DashboardOpModeController(OpModeManagerImpl manager) {
        this.opModeManager = manager;
        this.registeredOpModes = RegisteredOpModes.getInstance();

        try {
            refreshOpModeCache();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize OpMode cache in constructor", e);
        }
    }

    public void refreshOpModeCache() {
        try {
            Field opModesLockField = RegisteredOpModes.class.getDeclaredField("opModesLock");
            opModesLockField.setAccessible(true);
            Object lock = opModesLockField.get(registeredOpModes);

            synchronized (lock) {
                opModeMap.clear();

                Field opModeClassesField = RegisteredOpModes.class.getDeclaredField("opModeClasses");
                opModeClassesField.setAccessible(true);
                Map<String, ?> opModeClasses = (Map<String, ?>) opModeClassesField.get(registeredOpModes);

                Field opModeInstancesField = RegisteredOpModes.class.getDeclaredField("opModeInstances");
                opModeInstancesField.setAccessible(true);
                Map<String, ?> opModeInstances = (Map<String, ?>) opModeInstancesField.get(registeredOpModes);

                for (Map.Entry<String, ?> entry : opModeClasses.entrySet()) {
                    Object opModeMetaAndClass = entry.getValue();
                    Field metaField = opModeMetaAndClass.getClass().getDeclaredField("meta");
                    metaField.setAccessible(true);
                    OpModeMeta meta = (OpModeMeta) metaField.get(opModeMetaAndClass);
                    opModeMap.put(meta.name, meta);
                }

                for (Map.Entry<String, ?> entry : opModeInstances.entrySet()) {
                    Object opModeMetaAndInstance = entry.getValue();
                    Field metaField = opModeMetaAndInstance.getClass().getDeclaredField("meta");
                    metaField.setAccessible(true);
                    OpModeMeta meta = (OpModeMeta) metaField.get(opModeMetaAndInstance);
                    opModeMap.put(meta.name, meta);
                }

                Log.i(TAG, "Cached " + opModeMap.size() + " OpModes from RegisteredOpModes");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh OpMode cache", e);
            throw new RuntimeException(e);
        }
    }

    public void startOpMode(String name) {
        if (opModeManager == null || name == null) {
            Log.e(TAG, "Cannot start OpMode - opModeManager: " + (opModeManager != null) + ", name: " + name);
            return;
        }

        if (!opModeMap.containsKey(name)) {
            Log.e(TAG, "OpMode not found: " + name);
            return;
        }

        mainHandler.post(() -> {
            try {
                Log.i(TAG, "Attempting to start OpMode: " + name);

                opModeManager.initOpMode(name);
                opModeManager.startActiveOpMode();

                Log.i(TAG, "Started OpMode: " + name);
            } catch (Exception e) {
                Log.e(TAG, "Failed to start OpMode: " + name, e);
            }
        });
    }

    public void stopOpMode() {
        if (opModeManager == null) {
            Log.e(TAG, "Cannot stop OpMode - opModeManager is null");
            return;
        }

        mainHandler.post(() -> {
            try {
                Log.i(TAG, "Stopping active OpMode");
                opModeManager.stopActiveOpMode();
                Log.i(TAG, "OpMode stopped");
            } catch (Exception e) {
                Log.e(TAG, "Failed to stop OpMode", e);
            }
        });
    }

    public String getActiveOpModeName() {
        if (opModeManager == null) {
            return null;
        }
        return opModeManager.getActiveOpModeName();
    }

    public Set<String> getOpModeNames() {
        return opModeMap.keySet();
    }

    public Map<String, OpModeMeta> getOpModeMap() {
        return new HashMap<>(opModeMap);
    }
}