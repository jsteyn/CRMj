import crmj.CRMjProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        CRMjProperties properties;
        try {
            properties = new CRMjProperties();
        } catch (IOException e) {
            logger.error("Error loading properties: ".concat(e.getMessage()));
            return;
        }

        logger.info("Server opening at: http://localhost:3141");
        port(Integer.parseInt(properties.getPort()));
        get("/", (request, response) -> "hello world");

        properties.save();;
    }
}
