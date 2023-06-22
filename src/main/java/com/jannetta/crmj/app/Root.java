package com.jannetta.crmj.app;

import com.jannetta.crmj.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Root {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(Root.class);
    private final CRMjPropertiesManager m_propertiesManager;
    private final CRMjDatabaseManager m_databaseManager;
    private final CRMjServerManager m_serverManager;

    public Root() {
        CRMjPropertiesManager propertiesManager;
        try {
            propertiesManager = new CRMjPropertiesManager();
        } catch (IOException e) {
            s_LOGGER.error("Error loading properties: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize.");
        }
        m_propertiesManager = propertiesManager;

        m_databaseManager = new CRMjDatabaseManager(m_propertiesManager.getDatabaseDriver(), m_propertiesManager.getFullDatabaseJdbcUrl());
        m_serverManager = new CRMjServerManager(m_propertiesManager, m_databaseManager);

        if (m_propertiesManager.isDirty())
            m_propertiesManager.save();
    }

    public final CRMjPropertiesManager getPropertiesManager() {
        return m_propertiesManager;
    }

    public final CRMjServerManager getServerManager() {
        return m_serverManager;
    }

    public final CRMjDatabaseManager getDatabaseManager() {
        return m_databaseManager;
    }
}
