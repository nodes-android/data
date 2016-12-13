package dk.nodes.data.lib;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
/**
 * Created by joso on 30/05/16.
 */
public class CodeGenerator {

    private static final String SUFFIX = "Manager";

    private static String getPackageName(Elements elementUtils, AnnotatedClass annotatedClass) {
        PackageElement pkg = elementUtils.getPackageOf(annotatedClass.getTypeElement());
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
        return packageName;
    }

    public static void generateSerializedFileCode(Elements elementUtils, Filer filer, AnnotatedClass annotatedClass) throws IOException {
        String generatedClassName = annotatedClass.getTypeElement().getSimpleName() + SUFFIX;
        String packageName = getPackageName(elementUtils, annotatedClass);

        MethodSpec saveMethod = MethodSpec.methodBuilder("save")
                .addJavadoc("Writes object to openFileOutput().\n")
                .addParameter(ClassName.get(packageName, annotatedClass.getTypeElement().getSimpleName().toString()), "data")
                .addCode("" +
                        "try {\n" +
                        "\tfos = context.openFileOutput(\""+annotatedClass.getKey()+"\", Context.MODE_PRIVATE);\n" +
                        "\tos = new ObjectOutputStream(fos);\n" +
                        "\tos.writeObject(data);\n" +
                        "\tos.close();\n" +
                        "} catch(Exception e) {\n" +
                        "\t// Empty\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec loadMethod = MethodSpec.methodBuilder("load")
                .returns(ClassName.get(annotatedClass.getTypeElement()))
                .addJavadoc("Reads object from openFileInput().\n")
                .addJavadoc("@return " + annotatedClass.getTypeElement().getSimpleName() + " In case an exception is thrown, returns {@code new " + annotatedClass.getTypeElement().getSimpleName() + "()}\n")
                .addCode("" +
                        "try {\n" +
                        "\tfis = context.openFileInput(\""+annotatedClass.getKey()+"\");\n" +
                        "\tis = new ObjectInputStream(fis);\n" +
                        "\tObject input = is.readObject();\n" +
                        "\tis.close();\n" +
                        "\treturn ("+annotatedClass.getTypeElement().getSimpleName()+") input;\n" +
                        "} catch(Exception e) {\n" +
                        "\treturn new " + annotatedClass.getTypeElement().getSimpleName() + "();\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec manager = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(ClassName.get("java.io", "FileInputStream"), "fis", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "ObjectInputStream"), "is", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "FileOutputStream"), "fos", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "ObjectOutputStream"), "os", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "Context"), "context", Modifier.PRIVATE)
                .addMethod(loadMethod)
                .addMethod(saveMethod)
                .addMethod(generateContextConstructor())
                .build();

        // Write file
        JavaFile file = JavaFile.builder(packageName, manager).build();
        file.writeTo(filer);
    }

    public static void generateSerializedPrefsCode(Elements elementUtils, Filer filer, AnnotatedClass annotatedClass) throws IOException {
        String generatedClassName = annotatedClass.getTypeElement().getSimpleName() + SUFFIX;
        String packageName = getPackageName(elementUtils, annotatedClass);

        MethodSpec saveMethod = MethodSpec.methodBuilder("save")
                .addJavadoc("Writes object to SharedPreferences.\n")
                .addParameter(ClassName.get(packageName, annotatedClass.getTypeElement().getSimpleName().toString()), "data")
                .addCode("" +
                        "try {\n" +
                        "\tsharedPreferences = context.getSharedPreferences(\"appprefs\", Context.MODE_PRIVATE);\n" +
                        "\tbos = new ByteArrayOutputStream();\n" +
                        "\tos = new ObjectOutputStream( bos );\n" +
                        "\tos.writeObject(data);\n" +
                        "\tString encodedString = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);\n" +
                        "\tSharedPreferences.Editor editor = sharedPreferences.edit();\n" +
                        "\teditor.putString(\""+ annotatedClass.getKey()+"\", encodedString);\n" +
                        "\teditor.commit();\n" +
                        "\tbos.close();\n" +
                        "} catch(Exception e) {\n" +
                        "\t// Empty\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec loadMethod = MethodSpec.methodBuilder("load")
                .returns(ClassName.get(annotatedClass.getTypeElement()))
                .addJavadoc("Reads object from SharedPreferences.\n")
                .addJavadoc("@return " + annotatedClass.getTypeElement().getSimpleName() + " In case an exception is thrown, returns {@code new " + annotatedClass.getTypeElement().getSimpleName() + "()}\n")
                .addCode("" +
                        "try {\n" +
                        "\tsharedPreferences = context.getSharedPreferences(\"appprefs\", Context.MODE_PRIVATE);\n" +
                        "\tString inputString = sharedPreferences.getString(\"" + annotatedClass.getKey() + "\", \"nothing\");\n" +
                        "\tbyte[] byteData = Base64.decode(inputString, Base64.DEFAULT);\n" +
                        "\tbis = new ByteArrayInputStream(byteData);\n" +
                        "\tis = new ObjectInputStream(bis);\n" +
                        "\tObject input = is.readObject();\n" +
                        "\tbis.close();\n" +
                        "\treturn (" + annotatedClass.getTypeElement().getSimpleName() + ") input;\n" +
                        "} catch(Exception e) {\n" +
                        "\treturn new " + annotatedClass.getTypeElement().getSimpleName() + "();\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec manager = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(ClassName.get("java.io", "ObjectInputStream"), "is", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "ObjectOutputStream"), "os", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "ByteArrayInputStream"), "bis", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "ByteArrayOutputStream"), "bos", Modifier.PRIVATE)
                .addField(ClassName.get("android.util", "Base64"), "base64", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "SharedPreferences"), "sharedPreferences", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "Context"), "context", Modifier.PRIVATE)
                .addMethod(loadMethod)
                .addMethod(saveMethod)
                .addMethod(generateContextConstructor())
                .build();

        // Write file
        JavaFile file = JavaFile.builder(packageName, manager).build();
        file.writeTo(filer);
    }

    public static void generateGsonPrefsCode(Elements elementUtils, Filer filer, AnnotatedClass annotatedClass) throws IOException {
        String generatedClassName = annotatedClass.getTypeElement().getSimpleName() + SUFFIX;
        String packageName = getPackageName(elementUtils, annotatedClass);

        MethodSpec saveMethod = MethodSpec.methodBuilder("save")
                .addJavadoc("Writes object to SharedPreferences.\n")
                .addParameter(ClassName.get(packageName, annotatedClass.getTypeElement().getSimpleName().toString()), "data")
                .addCode("" +
                        "try {\n" +
                        "\tgson = new Gson();\n" +
                        "\tsharedPreferences = context.getSharedPreferences(\"appprefs\", Context.MODE_PRIVATE);\n" +
                        "\tString s = gson.toJson(data);\n" +
                        "\tSharedPreferences.Editor editor = sharedPreferences.edit();\n" +
                        "\teditor.putString(\"" + annotatedClass.getKey() + "\", s);\n" +
                        "\teditor.commit();\n" +
                        "} catch(Exception e) {\n" +
                        "\t// Empty\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec loadMethod = MethodSpec.methodBuilder("load")
                .returns(ClassName.get(annotatedClass.getTypeElement()))
                .addJavadoc("Reads object from SharedPreferences.\n")
                .addJavadoc("@return " + annotatedClass.getTypeElement().getSimpleName() + " In case an exception is thrown, returns {@code new " + annotatedClass.getTypeElement().getSimpleName() + "()}\n")
                .addCode("" +
                        "try {\n" +
                        "\tgson = new Gson();\n" +
                        "\tsharedPreferences = context.getSharedPreferences(\"appprefs\", Context.MODE_PRIVATE);\n" +
                        "\tString inputString = sharedPreferences.getString(\"" + annotatedClass.getKey() + "\", \"nothing\");\n" +
                        "\tObject input = gson.fromJson(inputString, " + annotatedClass.getTypeElement().getSimpleName() + ".class);\n" +
                        "\treturn (" + annotatedClass.getTypeElement().getSimpleName() + ") input;\n" +
                        "} catch(Exception e) {\n" +
                        "\treturn new " + annotatedClass.getTypeElement().getSimpleName() + "();\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec manager = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(ClassName.get("com.google.gson", "Gson"), "gson", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "SharedPreferences"), "sharedPreferences", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "Context"), "context", Modifier.PRIVATE)
                .addMethod(loadMethod)
                .addMethod(saveMethod)
                .addMethod(generateContextConstructor())
                .build();

        // Write file
        JavaFile file = JavaFile.builder(packageName, manager).build();
        file.writeTo(filer);
    }

    public static void generateGsonFileCode(Elements elementUtils, Filer filer, AnnotatedClass annotatedClass) throws IOException {
        String generatedClassName = annotatedClass.getTypeElement().getSimpleName() + SUFFIX;
        String packageName = getPackageName(elementUtils, annotatedClass);

        MethodSpec saveMethod = MethodSpec.methodBuilder("save")
                .addJavadoc("Writes object to openFileOutput().\n")
                .addParameter(ClassName.get(packageName, annotatedClass.getTypeElement().getSimpleName().toString()), "data")
                .addCode("" +
                        "try {\n" +
                        "\tgson = new Gson();\n" +
                        "\tString s = gson.toJson(data);\n" +
                        "\tosw = new OutputStreamWriter(context.openFileOutput(\"" + annotatedClass.getKey() + "\", Context.MODE_PRIVATE));\n" +
                        "\tosw.write(s);\n" +
                        "\tosw.close();\n" +
                        "} catch(Exception e) {\n" +
                        "\t// Empty\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec loadMethod = MethodSpec.methodBuilder("load")
                .returns(ClassName.get(annotatedClass.getTypeElement()))
                .addJavadoc("Reads object from openFileInput().\n")
                .addJavadoc("@return " + annotatedClass.getTypeElement().getSimpleName() + " In case an exception is thrown, returns {@code new " + annotatedClass.getTypeElement().getSimpleName() + "()}\n")
                .addCode("" +
                        "try {\n" +
                        "\tfis = context.openFileInput(\"" + annotatedClass.getKey() + "\");\n" +
                        "\tbr = new BufferedReader(new InputStreamReader(fis));\n" +
                        "\tStringBuilder sb = new StringBuilder();\n" +
                        "\tString line;\n" +
                        "\twhile ((line = br.readLine()) != null) {\n" +
                        "\t\tsb.append(line);\n" +
                        "\t}\n" +
                        "\tString json = sb.toString();\n" +
                        "\tObject input = gson.fromJson(json, Object.class);\n" +
                        "\tbr.close();\n" +
                        "\treturn (" + annotatedClass.getTypeElement().getSimpleName() + ") input;\n" +
                        "} catch(Exception e) {\n" +
                        "\treturn new " + annotatedClass.getTypeElement().getSimpleName() + "();\n" +
                        "}\n"
                )
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec manager = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(ClassName.get("java.io", "FileInputStream"), "fis", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "InputStreamReader"), "ir", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "BufferedReader"), "br", Modifier.PRIVATE)
                .addField(ClassName.get("java.io", "OutputStreamWriter"), "osw", Modifier.PRIVATE)
                .addField(ClassName.get("android.content", "Context"), "context", Modifier.PRIVATE)
                .addField(ClassName.get("com.google.gson", "Gson"), "gson", Modifier.PRIVATE)
                .addMethod(loadMethod)
                .addMethod(saveMethod)
                .addMethod(generateContextConstructor())
                .build();

        // Write file
        JavaFile file = JavaFile.builder(packageName, manager).build();
        file.writeTo(filer);
    }

    private static MethodSpec generateContextConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("this.$N = $N", "context", "context")
                .build();
    }

}
