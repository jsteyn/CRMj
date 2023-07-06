package com.jannetta.crmj.app;

import com.google.gson.Gson;
import com.jannetta.crmj.database.model.Contact;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;

public class CRMjServerAjaxManager {
    private final CRMjDatabaseManager m_databaseManager;

    public CRMjServerAjaxManager(CRMjDatabaseManager databaseManager) {
        m_databaseManager = databaseManager;
    }

    public void mapRoutes() {
        Spark.post("/get_contacts", this::getAllContacts);
    }

    private String getAllContacts(Request request, Response response) {
        response.type("application/json");

        m_databaseManager.open();
        List<Contact> contacts = m_databaseManager.getAllContacts();
        m_databaseManager.close();
        
        Gson gson = new Gson();
        return gson.toJson(contacts);
    }
}
