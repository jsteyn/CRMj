package com.jannetta.crmj.app;

import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

/**
 * Composite class of the {@link CRMjServerManager}, for managing template-specific operations and routes.
 */
public class CRMjServerTemplateManager {
    private final VelocityTemplateEngine m_engine;

    /**
     * Main index page (website root).
     */
    private static final TemplateViewRoute index = (request, response) -> basicTemplate("static/templates/index.vm");

    public CRMjServerTemplateManager() {
        m_engine = new VelocityTemplateEngine();
    }

    /**
     * Map all templated routes (web pages) to their corresponding url.
     */
    public void mapRoutes() {
        Spark.get("/", index, m_engine);
    }

    /**
     * Basic wrapper method to convert a template file directly into a {@link ModelAndView} without parameters.
     * @param templatePath Path to the static file containing the template.
     */
    private static ModelAndView basicTemplate(@NotNull String templatePath) {
        return new ModelAndView(new HashMap<>(), templatePath);
    }
}
