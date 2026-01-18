package ro.mastermindrobotics.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;

import ro.mastermindrobotics.annotation.Configurable;

@SupportedAnnotationTypes("com.example.annotations.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigurableProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) return false;

        try {
            JavaFileObject file = processingEnv.getFiler()
                    .createSourceFile("com.example.generated.ConfigurableRegistry");

            try (Writer w = file.openWriter()) {
                w.write("package com.example.generated;\n\n");
                w.write("import java.util.List;\n\n");
                w.write("public final class ConfigurableRegistry {\n");
                w.write("  private ConfigurableRegistry() {}\n\n");
                w.write("  public static final List<Class<?>> CLASSES = List.of(\n");

                boolean first = true;
                for (Element e : roundEnv.getElementsAnnotatedWith(Configurable.class)) {
                    if (e.getKind() != ElementKind.CLASS) continue;
                    if (!first) w.write(",\n");
                    w.write("    " + ((TypeElement) e).getQualifiedName() + ".class");
                    first = false;
                }

                w.write("\n  );\n");
                w.write("}\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
