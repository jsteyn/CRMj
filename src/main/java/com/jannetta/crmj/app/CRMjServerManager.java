package com.jannetta.crmj.app;

import com.jannetta.crmj.database.model.Contact;
import com.jannetta.crmj.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

public class CRMjServerManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServerManager.class);

    private final ServerProperties m_properties;
    private final CRMjDatabaseManager m_database;

    private final VelocityTemplateEngine m_engine;

    public CRMjServerManager(ServerProperties properties, CRMjDatabaseManager database) {
        m_properties = properties;
        m_database = database;

        m_engine = new VelocityTemplateEngine();

        configureServer();
        CRMjTemplates.mapRoutes(m_engine);

        Spark.awaitInitialization();
        s_LOGGER.info("Server initialization complete");
    }

    private void configureServer() {
        int port = m_properties.getPort();
        String url = String.format("http://localhost:%s", port);

        s_LOGGER.info("Server opening at: ".concat(url));

        Spark.port(port);
        Spark.staticFiles.location("/velocity");
    }
}
