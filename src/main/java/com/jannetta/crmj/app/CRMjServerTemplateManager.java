package com.jannetta.crmj.app;

import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

public class CRMjServerTemplateManager {
    private static final TemplateViewRoute index = (request, response) -> basicTemplate("static/templates/index.vm");

    public CRMjServerTemplateManager() {

    }

    public void mapRoutes(@NotNull VelocityTemplateEngine engine) {
        Spark.get("/", index, engine);
    }

    private static ModelAndView basicTemplate(@NotNull String templatePath) {
        return new ModelAndView(new HashMap<>(), templatePath);
    }
}
