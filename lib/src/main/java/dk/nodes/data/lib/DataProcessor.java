package dk.nodes.data.lib;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import dk.nodes.data.annotation.Data;
import dk.nodes.data.annotation.Mode;
import dk.nodes.data.annotation.Persistence;

/**
 * Thanks for the awesome tutorial: http://hannesdorfmann.com/annotation-processing/annotationprocessing101
 */
@AutoService(Processor.class)
public class DataProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, AnnotatedClass> annotatedClasses = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        try {
            // Scan classes
            for (Element annotatedElement : env.getElementsAnnotatedWith(Data.class)) {

                // Check if a class has been annotated with @Data
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    continue;
                }

                // We can cast it, because we know that it of ElementKind.CLASS
                TypeElement typeElement = (TypeElement) annotatedElement;

                AnnotatedClass annotatedClass = new AnnotatedClass(typeElement);

                if( annotatedClasses.containsKey(typeElement.getSimpleName()) ) {
                    error(typeElement, String.format("Already contains an element %s, cant create duplicate managers."));
                    return false;
                }

                annotatedClasses.put(typeElement.getSimpleName().toString(), annotatedClass);
            }

            // Generate code
            for (AnnotatedClass annotatedClass : annotatedClasses.values()) {
                if( annotatedClass.getPersistence().equals(Persistence.SERIALIZATION) ) {

                    if( annotatedClass.getMode().equals(Mode.FILE) ) {
                        CodeGenerator.generateSerializedFileCode(elementUtils, filer, annotatedClass);
                    } else {
                        CodeGenerator.generateSerializedPrefsCode(elementUtils, filer, annotatedClass);
                    }

                }

                else if( annotatedClass.getPersistence().equals(Persistence.GSON) ) {
                    if (annotatedClass.getMode().equals(Mode.FILE)) {
                        CodeGenerator.generateGsonFileCode(elementUtils, filer, annotatedClass);
                    } else {
                        CodeGenerator.generateGsonPrefsCode(elementUtils, filer, annotatedClass);
                    }
                }
            }

            // We can have several rounds of compilation, so avoid duplicates
            annotatedClasses.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
            //error(null, e.getMessage());
        }

        return true;
    }

    /**
     * Prints an error message
     *
     * @param e   The element which has caused the error. Can be null
     * @param msg The error message
     */
    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(Data.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
