package com.jannetta.crmj.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CRMjProperties {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjProperties.class);
    private static final Path s_CONFIG_DIRECTORY = Paths.get(System.getProperty("user.home"), ".CRMj");
    private static final Path s_PROPERTIES_FILEPATH = s_CONFIG_DIRECTORY.resolve("crmj.properties");

    private final String m_portPropID = "server.port";
    private int m_port = 3141;

    public CRMjProperties() throws IOException {
        s_LOGGER.info("Loading properties: ".concat(s_PROPERTIES_FILEPATH.toString()));
        if (Files.exists(s_PROPERTIES_FILEPATH)) {
            loadPropertiesFromFile();
        } else {
            s_LOGGER.info("Properties file not found. Creating default");
            savePropertiesToFile();
        }
    }

    public void save() {
        try {
            savePropertiesToFile();
        } catch (IOException e) {
            s_LOGGER.error("Error writing properties: ".concat(e.getMessage()));
        }
    }

    private void loadPropertiesFromFile() throws IOException {
        Properties properties = new Properties();

        FileInputStream stream = new FileInputStream(s_PROPERTIES_FILEPATH.toFile());
        properties.load(stream);
        stream.close();

        try {
            m_port = readIntProperty(properties, m_portPropID);
        } catch(MissingPropertyException e) {
            s_LOGGER.error(String.format(
                    "Error reading property [%s]. Property not found. Using default value of: %s",
                    m_portPropID, m_port
            ));
        } catch (NumberFormatException e) {
            s_LOGGER.error(String.format(
                    "Error reading property [%s]. Value [%s] not a valid integer. Using default value of: %s",
                    m_portPropID, properties.getProperty(m_portPropID), m_port
            ));
        }
    }

    private void savePropertiesToFile() throws IOException {
        if (!Files.exists(s_PROPERTIES_FILEPATH)) {
            Files.createDirectories(s_PROPERTIES_FILEPATH.getParent());
            Files.createFile(s_PROPERTIES_FILEPATH);
        }

        Properties properties = new Properties();

        properties.setProperty(m_portPropID, Integer.toString(m_port));

        FileOutputStream stream = new FileOutputStream(s_PROPERTIES_FILEPATH.toFile());
        properties.store(stream, "CRMj (Customer Relationship Management - Java/Jannetta) properties");
        stream.close();
    }

    private int readIntProperty(Properties properties, String propertyID)
            throws NumberFormatException, MissingPropertyException {
        String rawValue = readStringProperty(properties, propertyID);
        return Integer.parseInt(rawValue);
    }
    private float readFloatProperty(Properties properties, String propertyID)
            throws NumberFormatException, MissingPropertyException {
        String rawValue = readStringProperty(properties, propertyID);
        return Float.parseFloat(rawValue);
    }
    private String readStringProperty(Properties properties, String propertyID) throws MissingPropertyException {
        String value = properties.getProperty(propertyID);
        if (value == null)
            throw new MissingPropertyException(propertyID);
        return value;
    }

    public Path getConfigLocation() {
        return s_PROPERTIES_FILEPATH;
    }

    public int getPort() {
        return m_port;
    }

    static class MissingPropertyException extends Exception {
        public MissingPropertyException(String propertyID) {
            super(String.format("Property ID [%s] not found in resource file", propertyID));
        }
    }
}
