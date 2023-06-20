package com.jannetta.crmj.app;

import com.jannetta.crmj.dao.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Root {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(Root.class);
    private final CRMjPropertiesManager m_propertiesManager;
    private final DatabaseManager m_databaseManager;
    private final CRMjServerManager m_serverManager;

    public Root() {
        CRMjPropertiesManager propertiesManager;
        try {
            propertiesManager = new CRMjPropertiesManager();
        } catch (IOException e) {
            s_LOGGER.error("Error loading properties: {}", e.getMessage());
            m_propertiesManager = null;
            m_databaseManager = null;
            m_serverManager = null;
            return;
        }
        m_propertiesManager = propertiesManager;

        DatabaseManager databaseManager;
        try {
            databaseManager = new DatabaseManager(m_propertiesManager.getDatabaseUrl());
        } catch (SQLException e) {
            s_LOGGER.error("Error connecting to database: {}", e.getMessage());
            m_databaseManager = null;
            m_serverManager = null;
            return;
        }
        m_databaseManager = databaseManager;

        m_serverManager = new CRMjServerManager(m_propertiesManager);

        if (m_propertiesManager.isDirty())
            m_propertiesManager.save();
    }

    public final CRMjPropertiesManager getPropertiesManager() {
        return m_propertiesManager;
    }

    public final CRMjServerManager getServerManager() {
        return m_serverManager;
    }

    public final DatabaseManager getDatabaseManager() {
        return m_databaseManager;
    }
}
