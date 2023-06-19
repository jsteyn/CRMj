package com.jannetta.crmj.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Root {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(Root.class);
    private final CRMjProperties m_properties;
    private final CRMjServer m_server;

    public Root() {
        CRMjProperties properties;
        try {
            properties = new CRMjProperties();
        } catch (IOException e) {
            m_properties = null;
            m_server = null;
            s_LOGGER.error("Error loading properties: ".concat(e.getMessage()));
            return;
        }
        m_properties = properties;

        m_server = new CRMjServer(m_properties);
    }

    public final CRMjProperties getProperties() {
        return m_properties;
    }

    public final CRMjServer getServer() {
        return m_server;
    }
}
