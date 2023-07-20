package com.jannetta.crmj.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.HashMap;
import java.util.List;

public class CRMjServerAjaxManager {
    private final DatabaseManager m_databaseManager;

    public CRMjServerAjaxManager(@NotNull DatabaseManager databaseManager) {
        m_databaseManager = databaseManager;
    }

    public void mapRoutes() {
        Spark.post("/get_person_list_ranged", wrapAjax(this::getPersonListRanged));
    }

    private Route wrapAjax(Route route) {
        return (request, response) -> {
            try {
                return route.handle(request, response);
            } catch (Exception e) {
                return generateErrorReturn(e.getMessage());
            }
        };
    }

    private String getPersonListRanged(@NotNull Request request, @NotNull Response response) {
        response.type("application/json");

        int begin, amount;

        JsonObject output = new JsonObject();

        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        begin = parameters.get("begin").getAsInt();
        amount = parameters.get("amount").getAsInt();

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
            personData.addProperty("id", (Integer) record[0]);
            personData.addProperty("firstName", (String) record[1]);
            personData.addProperty("lastName", (String) record[2]);
            personList.add(personData);
        }

        output.add("people", personList);
        output.addProperty("success", true);

        return output.toString();
    }

    private String generateErrorReturn(String message) {
        JsonObject output = new JsonObject();
        output.addProperty("success", false);
        output.addProperty("error", message);
        return output.toString();
    }
}
