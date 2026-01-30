package ro.mastermindsrobotics.dashboard.module.core;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;
import ro.mastermindsrobotics.dashboard.generated.ConfigurableRegistry;
import ro.mastermindsrobotics.dashboard.module.AbstractDashboardModule;

public class ConfigurableVariablesModule extends AbstractDashboardModule {
    public static final String TAG = "ConfigurableVariablesModule";
    public static final Set<Class<?>> ALLOWED_TYPES = Set.of(
            Integer.class, int.class,
            Boolean.class, boolean.class,
            Double.class, double.class,
            Float.class, float.class,
            String.class
    );

    public LinkedHashMap<String, ClassMetadata> classesAndFields = new LinkedHashMap<>();

    @Override
    public String getRoute() {
        return "configurableVariables";
    }


    @Override
    public void init() {
        for (Class<?> clazz : ConfigurableRegistry.getRegisteredClasses()) {

            LinkedHashMap<String, Field> fields = new LinkedHashMap<>();
            for (Field field : clazz.getDeclaredFields())
                if (ALLOWED_TYPES.contains(field.getType())) {
                    field.setAccessible(true);
                    fields.put(field.getName(), field);
                }

            classesAndFields.put(clazz.getName(), new ClassMetadata(clazz, fields));
        }
        logClassesAndFields();
    }

    private NanoHTTPD.Response requestGET(NanoHTTPD.IHTTPSession session) {
        Map<String, String> params = session.getParms();
        String requestedClass = params.get("class");

        Log.d(TAG, "requestGET called, requestedClass: " + requestedClass);

        if (requestedClass != null) {
            ClassMetadata meta = classesAndFields.get(requestedClass);

            if (meta == null) {
                Log.d(TAG, "Class not found: " + requestedClass);
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.NOT_FOUND,
                        "application/json",
                        "{\"error\": \"Class not found: " + requestedClass + "\"}"
                );
            }

            List<Object> response = new ArrayList<>();
            response.add(buildClassObject(meta));

            String json = new com.google.gson.Gson().toJson(response);
            Log.d(TAG, "Single class response: " + json);

            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK,
                    "application/json",
                    json
            );
        }

        List<Object> response = new ArrayList<>();

        for (ClassMetadata meta : classesAndFields.values()) {
            response.add(buildClassObject(meta));
        }

        String json = new com.google.gson.Gson().toJson(response);
        Log.d(TAG, "All classes response: " + json);

        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                "application/json",
                json
        );
    }

    private Map<String, Object> buildClassObject(ClassMetadata meta) {
        Map<String, Object> classObj = new LinkedHashMap<>();
        classObj.put("class", meta.clazz.getName());

        List<Map<String, Object>> fields = new ArrayList<>();

        for (Map.Entry<String, Field> entry : meta.fields.entrySet()) {
            try {
                Object value = entry.getValue().get(null);
                Map<String, Object> field = new LinkedHashMap<>();

                field.put("var", entry.getKey());
                field.put("val", value);

                fields.add(field);

            } catch (IllegalAccessException ignored) {}
        }

        classObj.put("fields", fields);
        return classObj;
    }

    private void requestPUT() {

    }

    @Override
    public NanoHTTPD.Response onRequest(NanoHTTPD.IHTTPSession session) {
        Log.d(TAG, "Request received: " + session.getMethod() + " " + session.getUri());
        Log.d(TAG, "classesAndFields size: " + classesAndFields.size());

        switch (session.getMethod()) {
            case GET:
                NanoHTTPD.Response response = requestGET(session);
                Log.d(TAG, "Response created, status: " + response.getStatus());
                return response;
            default:
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED,
                        "application/json",
                        "{\"error\": \"Method not allowed\"}"
                );
        }
    }
    private void logClassesAndFields() {
        Log.d(TAG, "==== Configurable Classes Dump START ====");

        for (Map.Entry<String, ClassMetadata> classEntry : classesAndFields.entrySet()) {
            String classKey = classEntry.getKey();
            ClassMetadata metadata = classEntry.getValue();

            Log.d(TAG, "Class: " + classKey + " (" + metadata.clazz.getName() + ")");

            for (Map.Entry<String, Field> fieldEntry : metadata.fields.entrySet()) {
                String fieldName = fieldEntry.getKey();
                Field field = fieldEntry.getValue();

                Log.d(TAG, "Field: " + fieldName +
                        " | Type: " + field.getType().getSimpleName());
            }
        }

        Log.d(TAG, "==== Configurable Classes Dump END ====");
    }

    public static class ClassMetadata {
        private Class<?> clazz;
        private LinkedHashMap<String, Field> fields;

        public ClassMetadata(Class<?> clazz, LinkedHashMap<String, Field> fields) {
            this.clazz = clazz;
            this.fields = fields;
        }
    }
}

