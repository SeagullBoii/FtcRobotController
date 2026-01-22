package ro.mastermindsrobotics.dashboard.module.core;

import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

