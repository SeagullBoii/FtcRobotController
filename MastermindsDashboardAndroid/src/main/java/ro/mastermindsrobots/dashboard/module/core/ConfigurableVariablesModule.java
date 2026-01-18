package ro.mastermindsrobots.dashboard.module.core;

import android.util.Log;

import java.io.File;
import java.util.Objects;

import ro.mastermindsrobots.dashboard.module.AbstractDashboardModule;

public class ConfigurableVariablesModule extends AbstractDashboardModule {
    public static final String TAG = "ConfigurableVariablesModule";

    @Override
    public String getRoute() {
        return "configurableVariables";
    }

    public static void main(String[] args) {
        File javaFolder = new File("TeamCode/src/main/java");

        if (!javaFolder.exists() || !javaFolder.isDirectory()) {
            Log.e(TAG,"Java folder not found!");
            return;
        }

        listClasses(javaFolder, "");
    }

    private static void listClasses(File folder, String packageName) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                listClasses(file, newPackage);
            } else if (file.getName().endsWith(".java")) {
                String className = file.getName().replace(".java", "");
                String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;
                Log.i(TAG, fullClassName);
            }
        }
    }
}
