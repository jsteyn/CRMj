package crmj;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CRMjServer {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServer.class);
    private CRMjProperties m_properties;

    public CRMjServer() {
        try {
            m_properties = new CRMjProperties();
        } catch (IOException e) {
            s_LOGGER.error("Error loading properties: ".concat(e.getMessage()));
            return;
        }

        configureServer();
        configureWebpages();
        Spark.awaitInitialization();
        s_LOGGER.info("Server initialization complete");
    }

    private void configureServer() {
        int port = m_properties.getPort();
        String url = String.format("http://localhost:%s", port);

        s_LOGGER.info("Server opening at: ".concat(url));

        Spark.port(port);
    }

    private void configureWebpages() {
        VelocityTemplateEngine engine = new VelocityTemplateEngine();
        Spark.get(
            "/",
            (request, response) -> new ModelAndView(new HashMap<String, Object>(), "website/index.vm"),
            engine
        );
    }
}
