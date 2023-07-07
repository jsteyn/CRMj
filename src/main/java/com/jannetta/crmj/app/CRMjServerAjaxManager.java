package com.jannetta.crmj.app;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jannetta.crmj.database.model.Contact;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Arrays;
import java.util.List;

public class CRMjServerAjaxManager {
    private final CRMjDatabaseManager m_databaseManager;

    public CRMjServerAjaxManager(@NotNull CRMjDatabaseManager databaseManager) {
        m_databaseManager = databaseManager;
    }

    public void mapRoutes() {
        Spark.post("/get_contacts", this::getAllContacts);
        Spark.post("/add_contact", this::addContact);
    }

    private String getAllContacts(@NotNull Request request, @NotNull Response response) {
        response.type("application/json");

        m_databaseManager.open();
        List<Contact> contacts = m_databaseManager.getAllContacts();
        m_databaseManager.close();

        Gson gson = new Gson();
        return gson.toJson(contacts);
    }

    private String addContact(@NotNull Request request, @NotNull Response response) {
        response.type("application/json");

        Gson gson = new Gson();
        Contact contact = gson.fromJson(request.body(), Contact.class);
        m_databaseManager.open();
        m_databaseManager.insert(contact);
        m_databaseManager.close();

        JsonObject out = new JsonObject();
        out.addProperty("success", true);
        return out.toString();
    }
}