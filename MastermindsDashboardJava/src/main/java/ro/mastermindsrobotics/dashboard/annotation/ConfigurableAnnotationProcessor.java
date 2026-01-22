package ro.mastermindsrobotics.dashboard.annotation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Annotation processor for @Configurable annotations.
 * This processor generates a registry class that lists all @Configurable classes at compile time.
 * 
 * Note: Since @Configurable now has RUNTIME retention, runtime discovery via ConfigurableScanner
 * is the primary method. This processor provides compile-time validation and an optional
 * pre-generated registry for faster startup.
 * 
 * Registration is done via META-INF/services/javax.annotation.processing.Processor
 */
@SupportedAnnotationTypes("ro.mastermindsrobotics.dashboard.annotation.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigurableAnnotationProcessor extends AbstractProcessor {
    
    private boolean processed = false;
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed || annotations.isEmpty()) return false;

        try {
            List<String> classNames = new ArrayList<>();
            for (Element element : roundEnv.getElementsAnnotatedWith(Configurable.class)) {
                if (element.getKind() == ElementKind.CLASS) {
                    String className = ((TypeElement) element).getQualifiedName().toString();
                    classNames.add(className);
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.NOTE, 
                            "Found @Configurable class: " + className
                    );
                }
            }

            if (!classNames.isEmpty()) {
                generateRegistrationClass(classNames);
                processed = true;
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, 
                    "Error generating ConfigurableRegistry: " + e.getMessage());
        }
        return true;
    }

    private void generateRegistrationClass(List<String> classNames) throws IOException {
        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile("ro.mastermindsrobotics.dashboard.generated.ConfigurableRegistry");

        try (PrintWriter out = new PrintWriter(file.openWriter())) {
            out.println("package ro.mastermindsrobotics.dashboard.generated;");
            out.println();
            out.println("import java.util.ArrayList;");
            out.println("import java.util.Collections;");
            out.println("import java.util.List;");
            out.println();
            out.println("/**");
            out.println(" * Auto-generated registry of @Configurable classes.");
            out.println(" * Generated at compile time by ConfigurableAnnotationProcessor.");
            out.println(" */");
            out.println("public final class ConfigurableRegistry {");
            out.println();
            out.println("    private static final List<Class<?>> REGISTERED_CLASSES;");
            out.println();
            out.println("    static {");
            out.println("        List<Class<?>> classes = new ArrayList<>();");
            
            for (String className : classNames) {
                out.println("        classes.add(" + className + ".class);");
            }
            
            out.println("        REGISTERED_CLASSES = Collections.unmodifiableList(classes);");
            out.println("    }");
            out.println();
            out.println("    private ConfigurableRegistry() {");
            out.println("        // Prevent instantiation");
            out.println("    }");
            out.println();
            out.println("    /**");
            out.println("     * Gets the list of all @Configurable classes discovered at compile time.");
            out.println("     * @return Unmodifiable list of configurable classes");
            out.println("     */");
            out.println("    public static List<Class<?>> getRegisteredClasses() {");
            out.println("        return REGISTERED_CLASSES;");
            out.println("    }");
            out.println();
            out.println("    /**");
            out.println("     * Gets the number of registered @Configurable classes.");
            out.println("     * @return The count of registered classes");
            out.println("     */");
            out.println("    public static int getRegisteredClassCount() {");
            out.println("        return REGISTERED_CLASSES.size();");
            out.println("    }");
            out.println("}");
        }
    }
}
