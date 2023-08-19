package com.jannetta.crmj.appj;

import com.jannetta.crmj.app.CRMjPropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class JPropertiesManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjPropertiesManager.class);
    private static final String s_CONFIG_DIRECTORY = System.getProperty("user.home") + "/.CRMj";
    private static final String s_PROPERTIES_FILEPATH = s_CONFIG_DIRECTORY + "/jcrmj.properties";
    private final String m_portPropID = "server.port";
    private int m_port = 3141;
    private Properties prop = new Properties();

    public JPropertiesManager() {
        s_LOGGER.info("Loading properties from: [{}]", s_PROPERTIES_FILEPATH);
        if (Files.exists(Paths.get(s_PROPERTIES_FILEPATH))) {
            loadPropertiesFromFile();
        } else {
            s_LOGGER.info("Properties file not found. Creating default");
            savePropertiesToFile();
        }
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    private void loadPropertiesFromFile() {
        try (InputStream input = new FileInputStream(s_PROPERTIES_FILEPATH)) {
            // load a properties file
            prop.load(input);
            System.out.println(prop.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void savePropertiesToFile() {
        File propfile = new File(s_PROPERTIES_FILEPATH);
        try  {
            OutputStream output = new FileOutputStream(propfile);
            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("database.jdbc.url", "localhost");
            prop.setProperty("database.jdbc.protocol", "jdbc\\:sqlite\\:");
            prop.setProperty("server.port","3141");
            prop.setProperty("database.filepath", s_PROPERTIES_FILEPATH + "/" + "CRMj.db");

            // save properties to project root folder
            prop.store(output, null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
