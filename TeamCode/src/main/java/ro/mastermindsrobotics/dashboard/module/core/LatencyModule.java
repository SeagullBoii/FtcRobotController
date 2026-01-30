package ro.mastermindsrobotics.dashboard.module.core;

import android.util.Log;

import com.google.gson.annotations.Expose;
import fi.iki.elonen.NanoHTTPD;
import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;

import java.util.concurrent.atomic.AtomicLong;

public class LatencyModule extends AbstractDashboardModule {
    private final AtomicLong lastLatency = new AtomicLong(-1);

    @Override
    public String getRoute() {
        return "latency";
    }

    @Override
    public NanoHTTPD.Response onRequest(NanoHTTPD.IHTTPSession session) {
        try {
            String tsParam = session.getParms().get("ts");
            long clientTs = tsParam != null ? Long.parseLong(tsParam) : -1;

            long serverTs = System.currentTimeMillis();
            lastLatency.set(serverTs - clientTs);

            String json = "{ \"serverTime\": " + serverTs + " }";

            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK,
                    "application/json",
                    json
            );

        } catch (Exception e) {
            Log.e("LatencyModule", "Failed request", e);
            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
                    "text/plain",
                    "Invalid timestamp"
            );
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
