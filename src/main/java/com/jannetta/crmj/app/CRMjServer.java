package com.jannetta.crmj.app;

import com.jannetta.crmj.controller.ContactController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.IOException;
import java.util.HashMap;

import static spark.Spark.*;

public class CRMjServer {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServer.class);
    private final CRMjProperties m_properties;

    public CRMjServer(CRMjProperties properties) {
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
        get("/contact", ContactController.getContact);
        post("/add_contact", ContactController::addContact);

        // Returns plain text
        get("/username/:email", (request, response) -> {
            return ContactController.getUsername(request.params(":email"));
        });

        // Returns JSON
        /**
         * Example URL: http://localhost:3141/j_username/jannetta@henning.org
         */
        get("/j_username/:email", (request, response) -> {
            return ContactController.getJSONUsername(request.params(":email"));
        });

    }
}
