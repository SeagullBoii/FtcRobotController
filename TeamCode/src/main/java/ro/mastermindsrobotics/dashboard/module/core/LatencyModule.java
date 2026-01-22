package ro.mastermindsrobotics.dashboard.module.core;

import android.util.Log;

import com.google.gson.annotations.Expose;
import fi.iki.elonen.NanoHTTPD;
import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Measures latency between dashboard client and robot.
 */
public class LatencyModule extends AbstractDashboardModule {
    private final AtomicLong lastLatency = new AtomicLong(-1);

    @Override
    public String getRoute() {
        return "latency";
    }

    @Override
    public void onRequest(NanoHTTPD.IHTTPSession session) {
        try {
            String ts = session.getParms().get("ts");
            if (ts != null) {
                lastLatency.set(Long.parseLong(ts));
            }
        } catch (Exception e) {
            Log.e("LatencyModule", "Failed request: ", e);
        }
    }

    @Override
    public Object returnData() {
        return new LatencyData(lastLatency.get());
    }

    private static class LatencyData {
        @Expose
        public final long latencyMs;

        public LatencyData(long latencyMs) {
            this.latencyMs = latencyMs;
        }
    }
}
