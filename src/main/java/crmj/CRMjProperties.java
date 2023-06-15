package crmj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class CRMjProperties {
    private static final Logger s_logger = LoggerFactory.getLogger(CRMjProperties.class);
    private static final File s_propertiesFileLocation = new File(System.getProperty("user.home").concat("/.CRMj/crmj.properties"));

    private final String m_portPropID = "server.port";
    private String m_port = "3141";

    public CRMjProperties() throws IOException {
        s_logger.info("Loading properties: ".concat(s_propertiesFileLocation.toString()));
        if (s_propertiesFileLocation.exists())
            loadPropertiesFromFile();
        else
            savePropertiesToFile();
    }

    public void save() {
        try {
            savePropertiesToFile();
        } catch (IOException e) {
            s_logger.error("Error writing properties: ".concat(e.getMessage()));
        }
    }

    private void loadPropertiesFromFile() throws IOException {
        Properties properties = new Properties();

        FileInputStream stream = new FileInputStream(s_propertiesFileLocation);
        properties.load(stream);
        stream.close();

        m_port = properties.getProperty(m_portPropID);
    }

    private void savePropertiesToFile() throws IOException {
        s_logger.info("Properties file not found. Creating default");
        //noinspection ResultOfMethodCallIgnored
        s_propertiesFileLocation.getParentFile().mkdirs();
        //noinspection ResultOfMethodCallIgnored
        s_propertiesFileLocation.createNewFile();

        Properties properties = new Properties();

        properties.setProperty(m_portPropID, m_port);

        FileOutputStream stream = new FileOutputStream(s_propertiesFileLocation);
        properties.store(stream, "CRMj (Customer Relationship Management - Java/Jannetta) properties");
        stream.close();
    }

    public String getPort() {
        return m_port;
    }
}
