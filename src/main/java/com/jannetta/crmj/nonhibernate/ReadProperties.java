package com.jannetta.crmj.nonhibernate;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Singleton to load properties
 */
public class ReadProperties {
    static Properties prop = new Properties();
    static ReadProperties readProperties = null;
    private static final Path s_CONFIG_DIRECTORY = Paths.get(System.getProperty("user.home"), ".CRMj");


    private ReadProperties() {
        try {
            InputStream input = new FileInputStream(s_CONFIG_DIRECTORY + "/crmj.properties");
            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static public ReadProperties getInstance() {
        if (readProperties == null) {
            readProperties = new ReadProperties();
        }
        return readProperties;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
