import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Server opening at: http://localhost:3141");
        port(3141);
        get("/", (request, response) -> "hello world");
    }
}
