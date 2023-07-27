package com.jannetta.crmj.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.database.DatabaseManager;
import com.jannetta.crmj.database.model.Person;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.HashMap;
import java.util.List;

public class CRMjServerAjaxManager {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(CRMjServerAjaxManager.class);

    private final DatabaseManager m_databaseManager;

    public CRMjServerAjaxManager(@NotNull DatabaseManager databaseManager) {
        m_databaseManager = databaseManager;
    }

    public void mapRoutes() {
        Spark.post("/get_person", wrapAjax(this::getPerson));
        Spark.post("/get_person_list_ranged", wrapAjax(this::getPersonListRanged));
        Spark.post("/add_person", wrapAjax(this::addPerson));
        Spark.post("/update_person", wrapAjax(this::updatePerson));
        Spark.post("/remove_person", wrapAjax(this::removePerson));
    }

    private Route wrapAjax(Route route) {
        return (request, response) -> {
            try {
                response.type("application/json");

                JsonObject output = (JsonObject) route.handle(request, response);
                output.addProperty("success", true);
                return output;
            } catch (Exception e) {
                s_LOGGER.error("Error running ajax operation", e);
                return generateErrorReturn(e.getMessage());
            }
        };
    }

    private @NotNull JsonObject getPerson(@NotNull Request request, @NotNull Response response) {
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();

        Person person;
        JsonObject output = new JsonObject();
        try (m_databaseManager) {
            m_databaseManager.open();
            HashMap<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", personId);
            person = m_databaseManager.readFrom(Person.class, "m_id = :id", queryParams).get(0);

            output.add("person", new Gson().toJsonTree(person));
        }

        return output;
    }

    private JsonObject getPersonListRanged(@NotNull Request request, @NotNull Response response) {
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int begin = parameters.get("begin").getAsInt();
        int amount = parameters.get("amount").getAsInt();

        List<Object[]> personListRaw;
        try (m_databaseManager) {
            m_databaseManager.open();
            HashMap<String, Object> queryParams = new HashMap<>();
            personListRaw = m_databaseManager.readLimit(
                "SELECT m_id, m_firstName, m_lastName " +
                    "FROM Person",
                queryParams, amount, begin);
        }

        JsonArray personList = new JsonArray();
        for (Object[] record : personListRaw) {
            JsonObject personData = new JsonObject();
            personData.addProperty("personId", (Integer) record[0]);
            personData.addProperty("firstName", (String) record[1]);
            personData.addProperty("lastName", (String) record[2]);
            personList.add(personData);
        }

        JsonObject output = new JsonObject();
        output.add("people", personList);

        return output;
    }

    private JsonObject addPerson(@NotNull Request request, @NotNull Response response) {
        Person person = new Gson().fromJson(request.body(), Person.class);

        try (m_databaseManager) {
            m_databaseManager.open();
            m_databaseManager.create(person);
        }

        return new JsonObject();
    }

    private JsonObject updatePerson(@NotNull Request request, @NotNull Response response) {
        Person person = new Gson().fromJson(request.body(), Person.class);

        try (m_databaseManager) {
            m_databaseManager.open();
            HashMap<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", person.getId());
            Person existing = m_databaseManager.readFrom(Person.class, "m_id = :id", queryParams).get(0);
            existing.setFirstName(person.getFirstName());
            existing.setMiddleNames(person.getMiddleNames());
            existing.setLastName(person.getLastName());
            existing.setTitle(person.getTitle());
            m_databaseManager.update(existing);
        }

        return new JsonObject();
    }

    private JsonObject removePerson(@NotNull Request request, @NotNull Response response) {
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int id = parameters.get("personId").getAsInt();

        try (m_databaseManager) {
            m_databaseManager.open();
            HashMap<String, Object> queryParams = new HashMap<>();
            queryParams.put("id", id);
            Person person = m_databaseManager.readFrom(Person.class, "m_id = :id", queryParams).get(0);
            m_databaseManager.delete(person);
        }

        return new JsonObject();
    }

    private String generateErrorReturn(String message) {
        JsonObject output = new JsonObject();
        output.addProperty("success", false);
        output.addProperty("error", message);
        return output.toString();
    }
}
