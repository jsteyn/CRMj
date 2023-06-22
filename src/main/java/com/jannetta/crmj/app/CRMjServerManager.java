package com.jannetta.crmj.app;

import com.jannetta.crmj.database.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class CRMjServerManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServerManager.class);
    private final CRMjPropertiesManager m_properties;
    private final CRMjDatabaseManager m_database;

    public CRMjServerManager(CRMjPropertiesManager properties, CRMjDatabaseManager database) {
        m_properties = properties;
        m_database = database;

        configureServer();
        configureWebpages();
        Spark.awaitInitialization();
        s_LOGGER.info("Server initialization complete");
    }

    private void configureServer() {
        int port = m_properties.getPort();
        String url = String.format("http://localhost:%s", port);

        s_LOGGER.info("Server opening at: ".concat(url));

        port(port);
        staticFiles.location("/velocity");
    }

    private void configureWebpages() {
        VelocityTemplateEngine engine = new VelocityTemplateEngine();

        get("/", simpleRouteFromUrl("velocity/index.vm"), engine);
        get("/add_contact", simpleRouteFromUrl("velocity/add_contact.vm"), engine);
        post("/contact_added", addContactRequest(), engine);
        get("/all_contacts", listContactRequest(), engine);
    }

    private TemplateViewRoute simpleRouteFromUrl(String url) {
        return (request, response) -> new ModelAndView(new HashMap<>(), url);
    }

    private TemplateViewRoute addContactRequest() {
        return (request, response) -> {
            Contact contact = new Contact();
            contact.setFirstName(request.queryParams("firstname"));
            contact.setMiddleNames(request.queryParams("middlename"));
            contact.setLastName(request.queryParams("lastname"));

            m_database.open();
            m_database.insert(contact);
            m_database.close();

            HashMap<String, Object> model = new HashMap<>();
            model.put("contact", contact);
            return new ModelAndView(model, "velocity/contact_added.vm");
        };
    }

    private TemplateViewRoute listContactRequest() {
        return (request, response) -> {
            HashMap<String, Object> model = new HashMap<>();

            m_database.open();
            model.put("allContacts", m_database.getAllContacts());
            m_database.close();

            return new ModelAndView(model, "velocity/all_contacts.vm");
        };
    }
}
