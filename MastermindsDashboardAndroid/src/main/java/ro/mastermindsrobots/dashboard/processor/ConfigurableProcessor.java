package ro.mastermindsrobots.dashboard.processor;

import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import ro.mastermindrobotics.annotation.Configurable;

@SupportedAnnotationTypes("ro.mastermindsrobotics.dashboard.annotations.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigurableProcessor extends AbstractProcessor {
    private static final String PACKAGE = "ro.mastermindsrobotics.dashboard.generated";
    private static final String CLASS_NAME = "ConfigurableRegistry";

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
            return false;
        }

        try {
            generateRegistry(roundEnv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void generateRegistry(RoundEnvironment roundEnv) throws Exception {
        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(PACKAGE + "." + CLASS_NAME);

        try (Writer writer = file.openWriter()) {

            writer.write("package " + PACKAGE + ";\n\n");
            writer.write("import java.util.List;\n\n");
            writer.write("public final class " + CLASS_NAME + " {\n\n");
            writer.write("  private " + CLASS_NAME + "() {}\n\n");
            writer.write("  public static final List<Class<?>> CLASSES = List.of(\n");

            boolean first = true;
            for (Element element : roundEnv.getElementsAnnotatedWith(Configurable.class)) {
                if (element.getKind() != ElementKind.CLASS) continue;

                TypeElement type = (TypeElement) element;
                if (!first) writer.write(",\n");
                writer.write("    " + type.getQualifiedName() + ".class");
                first = false;
            }

            writer.write("\n  );\n");
            writer.write("}\n");
        }
    }
}
