package com.jannetta.crmj.app;

import com.jannetta.crmj.database.DatabaseProperties;
import com.jannetta.crmj.server.ServerProperties;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Properties Manager implementation class for handling and serializing configurable properties.
 */
public class CRMjPropertiesManager implements DatabaseProperties, ServerProperties {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjPropertiesManager.class);
    private static final Path s_CONFIG_DIRECTORY = Paths.get(System.getProperty("user.home"), ".CRMj");
    private static final Path s_PROPERTIES_FILEPATH = s_CONFIG_DIRECTORY.resolve("crmj.properties");

    private final String m_portPropID = "server.port";
    private int m_port = 3141;

    private final String m_databaseDriverPropID = "database.driver";
    private String m_databaseDriver = "org.sqlite.JDBC";
    private final String m_databasePathPropID = "database.jdbc.url";
    private Path m_databasePath = s_CONFIG_DIRECTORY.resolve("data.db");
    private final String m_databaseProtocolPropID = "database.jdbc.protocol";
    private String m_databaseProtocol = "jdbc:sqlite:";
    private final String m_databaseDialectPropID = "database.jdbc.dialect";
    private String m_databaseDialect = "org.sqlite.hibernate.dialect.SQLiteDialect";

    private boolean m_isDirty = false;

    /**
     * Create a new {@link CRMjPropertiesManager}, and load any existing properties from the
     * {@link CRMjPropertiesManager#s_PROPERTIES_FILEPATH properties file}. If no such file exists, it will be created
     * and written to.
     * @throws IOException If an I/O error occurs.
     */
    public CRMjPropertiesManager() throws IOException {
        s_LOGGER.info("Loading properties from: [{}]", s_PROPERTIES_FILEPATH);
        if (Files.exists(s_PROPERTIES_FILEPATH)) {
            loadPropertiesFromFile();
        } else {
            s_LOGGER.info("Properties file not found. Creating default");
            savePropertiesToFile();
        }
    }

    /**
     * Write all properties to the {@link CRMjPropertiesManager#s_PROPERTIES_FILEPATH properties file} or log an error
     * on failure to write.
     * <br>
     * This operation will mark the manager as {@link CRMjPropertiesManager#isDirty() clean} on success.
     */
    public void save() {
        try {
            savePropertiesToFile();
        } catch (IOException e) {
            s_LOGGER.error("Error writing properties:", e);
        }
    }

    /**
     * Read all properties from the {@link CRMjPropertiesManager#s_PROPERTIES_FILEPATH properties file} or log an error
     * on failure to read.
     * <br>
     * This operation will mark the manager as {@link CRMjPropertiesManager#isDirty() clean} on success.
     */
    public void load() {
        try {
            loadPropertiesFromFile();
        } catch (IOException e) {
            s_LOGGER.error("Error reading properties:", e);
        }
    }

    public Path getConfigLocation() {
        return s_PROPERTIES_FILEPATH;
    }

    public int getPort() {
        return m_port;
    }

    public void setPort(int port) {
        m_port = port;
        m_isDirty = true;
    }

    public String getDatabaseDriver() {
        return m_databaseDriver;
    }

    public void setDatabaseDriver(@NotNull String databaseDriver) {
        m_databaseDriver = databaseDriver;
        m_isDirty = true;
    }

    public Path getDatabasePath() {
        return m_databasePath;
    }

    public void setDatabasePath(@NotNull Path path) {
        m_databasePath = path;
        m_isDirty = true;
    }

    public String getDatabaseProtocol() {
        return m_databaseProtocol;
    }

    public void setDatabaseProtocol(@NotNull String protocol) {
        m_databaseProtocol = protocol;
        m_isDirty = true;
    }

    public String getDatabaseDialect() {
        return m_databaseDialect;
    }

    public void setDatabaseDialect(@NotNull String protocol) {
        m_databaseDialect = protocol;
        m_isDirty = true;
    }

    public String getFullDatabaseUrl() {
        return String.format("%s%s", m_databaseProtocol, m_databasePath.toString());
    }

    /**
     * {@link CRMjPropertiesManager} is considered dirty if a property has been set to a new value since loading.
     * <br>
     * This object can only be marked clean again by saving or loading. Setting a property back to its original value
     * will not reset the dirty state of this object.
     * @return {@code true} if marked as dirty, otherwise false.
     */
    public boolean isDirty() {
        return m_isDirty;
    }

    private void loadPropertiesFromFile() throws IOException {
        m_isDirty = false;

        Properties properties = new Properties();

        try (FileInputStream stream = new FileInputStream(s_PROPERTIES_FILEPATH.toFile())) {
            properties.load(stream);
        }

        // Server
        m_port = readIntProperty(properties, m_portPropID, m_port);

        // Database
        m_databaseDriver = readStringProperty(properties, m_databaseDriverPropID, m_databaseDriver);
        m_databasePath = Paths.get(readStringProperty(properties, m_databasePathPropID, m_databasePath.toString()));
        m_databaseProtocol = readStringProperty(properties, m_databaseProtocolPropID, m_databaseProtocol);
        m_databaseDialect = readStringProperty(properties, m_databaseDialectPropID, m_databaseDialect);
    }

    private void savePropertiesToFile() throws IOException {
        if (!Files.exists(s_PROPERTIES_FILEPATH)) {
            Files.createDirectories(s_PROPERTIES_FILEPATH.getParent());
            Files.createFile(s_PROPERTIES_FILEPATH);
        }

        Properties properties = new Properties();

        // Server
        properties.setProperty(m_portPropID, Integer.toString(m_port));

        // Database
        properties.setProperty(m_databaseDriverPropID, m_databaseDriver);
        properties.setProperty(m_databasePathPropID, m_databasePath.toString());
        properties.setProperty(m_databaseProtocolPropID, m_databaseProtocol);
        properties.setProperty(m_databaseDialectPropID, m_databaseDialect);

        FileOutputStream stream = new FileOutputStream(s_PROPERTIES_FILEPATH.toFile());
        properties.store(stream, "CRMj (Customer Relationship Management - Java/Jannetta) properties");
        stream.close();

        m_isDirty = false;
    }

    /**
     * Performs the {@link CRMjPropertiesManager#readStringProperty(Properties, String, String)} operation and returns
     * the resultant property cast to an integer.
     * <br>
     * If the property exists but cannot be cast to an integer, the default value will be returned and an error will be
     * logged.
     * @see CRMjPropertiesManager#readStringProperty(Properties, String, String)
     * @return Property from {@code properties} with a key matching {@code propertyID}, or {@code defaultValue} if no
     * such property is found or if it cannot be cast to an integer.
     */
    private int readIntProperty(@NotNull Properties properties, @NotNull String propertyID, int defaultValue) {
        String rawValue = readStringProperty(properties, propertyID, null);
        if (rawValue == null) {
            m_isDirty = true;
            return defaultValue;
        }
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            s_LOGGER.error(
                "Error reading property [{}]. Value [{}] not a valid integer. Using default value of: {}",
                m_portPropID, properties.getProperty(m_portPropID), m_port
            );
            m_isDirty = true;
            return defaultValue;
        }
    }

    /**
     * Performs the {@link CRMjPropertiesManager#readStringProperty(Properties, String, String)} operation and returns
     * the resultant property cast to a float.
     * <br>
     * If the property exists but cannot be cast to a float, the default value will be returned and an error will be
     * logged.
     * @see CRMjPropertiesManager#readStringProperty(Properties, String, String)
     * @return Property from {@code properties} with a key matching {@code propertyID}, or {@code defaultValue} if no
     * such property is found or if it cannot be cast to a float.
     */
    private float readFloatProperty(@NotNull Properties properties, @NotNull String propertyID, float defaultValue) {
        String rawValue = readStringProperty(properties, propertyID, null);
        if (rawValue == null) {
            m_isDirty = true;
            return defaultValue;
        }
        try {
            return Float.parseFloat(rawValue);
        } catch (NumberFormatException e) {
            s_LOGGER.error(
                    "Error reading property [{}]. Value [{}] not a valid float. Using default value of: {}",
                    m_portPropID, properties.getProperty(m_portPropID), m_port
            );
            m_isDirty = true;
            return defaultValue;
        }
    }

    /**
     * Read property in {@code properties} matching {@code propertyId} as a {@code String}.
     * <br>
     * If the property is missing or unable to be read, {@code defaultValue} will be returned and a warning will be
     * logged.
     * @param properties Property handler to be read from.
     * @param propertyID Key of the property to read.
     * @param defaultValue Value to return if no property is found.
     * @return Property from {@code properties} with a key matching {@code propertyID}, or {@code defaultValue} if no
     * such property is found.
     */
    private String readStringProperty(@NotNull Properties properties, @NotNull String propertyID, String defaultValue){
        String value = properties.getProperty(propertyID);
        if (value == null) {
            s_LOGGER.warn(String.format(
                "Error reading property [%s]. Property not found. Using default value of: %s",
                propertyID, defaultValue
            ));
            m_isDirty = true;
            return defaultValue;
        }
        return value;
    }
}
