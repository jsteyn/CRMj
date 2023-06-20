package com.jannetta.crmj.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Root {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(Root.class);
    private final CRMjPropertiesManager m_properties;
    private final CRMjServerManager m_server;

    public Root() {
        CRMjPropertiesManager properties;
        try {
            properties = new CRMjPropertiesManager();
        } catch (IOException e) {
            m_properties = null;
            m_server = null;
            s_LOGGER.error("Error loading properties: ".concat(e.getMessage()));
            return;
        }
        m_properties = properties;

        m_server = new CRMjServerManager(m_properties);
    }

    public final CRMjPropertiesManager getProperties() {
        return m_properties;
    }

    public final CRMjServerManager getServer() {
        return m_server;
    }
}
