package com.jannetta.crmj.app;

import com.jannetta.crmj.database.DatabaseManager;
import com.jannetta.crmj.server.ServerProperties;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

/**
 * Server Manager implementation class for handling server initialization and route management.
 */
public class CRMjServerManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServerManager.class);

    private final ServerProperties m_properties;
    private final DatabaseManager m_databaseManager;

    private final CRMjServerTemplateManager m_templateManager;
    private final CRMjServerAjaxManager m_ajaxManager;

    /**
     * Create a new {@link CRMjServerManager} and initialize the server.
     * @param properties Properties manager containing server configuration properties.
     * @param databaseManager Database manager with read/write access.
     */
    public CRMjServerManager(@NotNull ServerProperties properties, @NotNull DatabaseManager databaseManager) {
        m_properties = properties;
        m_databaseManager = databaseManager;

        m_templateManager = new CRMjServerTemplateManager();
        m_ajaxManager = new CRMjServerAjaxManager(m_databaseManager);

        configureServer();
        m_templateManager.mapRoutes();
        m_ajaxManager.mapRoutes();

        Spark.awaitInitialization();
        s_LOGGER.info("Server initialization complete");
    }

    private void configureServer() {
        int port = m_properties.getPort();
        String url = String.format("http://localhost:%s", port);

        s_LOGGER.info("Server opening at: ".concat(url));

        Spark.port(port);
        Spark.staticFiles.location("static");
    }
}
