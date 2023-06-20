package com.jannetta.crmj.template;

import org.apache.velocity.app.*;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.*;

import java.nio.file.Path;
import java.util.Map;

public class TemplateManager {

    // Renders a template given a model and a request
    // The request is needed to check the user session for language settings
    // and to see if the user is logged in
    public static String render(Request request, Map<String, Object> model, Path template) {
        return strictVelocityEngine().render(new ModelAndView(model, template.toString()));
    }


    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}