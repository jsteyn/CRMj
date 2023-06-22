package com.jannetta.crmj.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class CRMjServerManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServerManager.class);
    private final CRMjPropertiesManager m_properties;

    public CRMjServerManager(CRMjPropertiesManager properties) {
        m_properties = properties;

        configureServer();
        configureWebpages();
        Spark.awaitInitialization();
        s_LOGGER.info("Server initialization complete");
    }

    private void configureServer() {
        int port = m_properties.getPort();
        String url = String.format("http://localhost:%s", port);

        s_LOGGER.info("Server opening at: ".concat(url));

        port(port);
        staticFiles.location("/website");
    }

    private void configureWebpages() {
        VelocityTemplateEngine engine = new VelocityTemplateEngine();
        get(
            "/",
            (request, response) -> new ModelAndView(new HashMap<String, Object>(), "website/index.vm"),
            engine
        );
    }
}
