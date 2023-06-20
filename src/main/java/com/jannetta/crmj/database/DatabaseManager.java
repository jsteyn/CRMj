package com.jannetta.crmj.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private Connection m_connection = null;
    private DatabaseMetaData m_metaData = null;
    private String m_url;

    /**
     * Construct new {@code DatabaseManager} from an existing database URL.
     * @param databaseURL Database URL to connect to.
     */
    public DatabaseManager(String databaseURL) throws SQLException {
        m_url = databaseURL;

        m_connection = DriverManager.getConnection(m_url);
        m_metaData = m_connection.getMetaData();
        s_LOGGER.info("Database connection successfully created at [{}]", m_url);
    }

    public ResultSet query(String query) {
        try {
            return m_connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            s_LOGGER.info("Error executing query - {}:\n{}", e.getMessage(), query);
            return null;
        }
    }
}
