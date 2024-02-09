package com.aldogg.sorter.code_generator;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class CodeGenerator {

    public static void main(String[] args) {
        // Initialize the Velocity engine
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "file");
        properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(properties);

        // Load the template
        Template template = velocityEngine.getTemplate("TemplateSorterUtilsType.vm");

        // Set up the context with our template variables
        VelocityContext context = new VelocityContext();

        context.put("type", "float");
        context.put("Type", "Float");
        context.put("TypeObject", "Float");
        context.put("typeMask", "int");
        context.put("mapF", "Float.floatToRawIntBits");

//        context.put("type", "long");
//        context.put("Type", "Long");
//        context.put("TypeObject", "Long");
//        context.put("typeMask", "long");

        /*
        context.put("type", "int");
        context.put("Type", "Int");
        context.put("TypeObject", "Integer");
        context.put("typeMask", "int");
         */
        context.put("utils", new StringUtils());

        // Generate the Java code
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Print the generated code
        System.out.println(writer);
    }
}
