package com.jannetta.crmj.app;

import spark.ModelAndView;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

public class CRMjTemplates {
    private static final TemplateViewRoute index = (request, response) -> basicTemplate("static/templates/index.vm");

    public static void mapRoutes(VelocityTemplateEngine engine) {
        Spark.get("/", CRMjTemplates.index, engine);
    }

    private static ModelAndView basicTemplate(String templatePath) {
        return new ModelAndView(new HashMap<>(), templatePath);
    }
}
