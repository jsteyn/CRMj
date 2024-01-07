package com.jannetta.crmj.appj.nonhibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.appj.JPropertiesManager;
import com.jannetta.crmj.database.model.People;
import com.jannetta.crmj.database.model.Person;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;

import java.sql.*;

import static com.jannetta.crmj.appj.nonhibernate.NonHibernateQueries.*;


public class PersonQueries {
    private static JPropertiesManager properties = new JPropertiesManager();

    public static Person getPerson(int id) {

        String query = "select person_id, date_of_birth, first_name, last_name, middle_names, title," +
                "nick_name, maiden_name, married_name from people where person_id " + "= '" + id + "'";
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        Person person = new Person();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            // loop through the result set
            while (rs.next()) {
                populatePerson(person, rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return person;
    }

    public static JsonObject getPerson(Request request, Response response) {
        response.type("application/json");
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        String query = "SELECT person_id, date_of_birth, first_name, last_name, middle_names, title," +
                "nick_name, maiden_name, married_name FROM people WHERE person_id " + " = '" + personId +
                "' ORDER BY last_name";
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        Person person = new Person();
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                populatePerson(person, rs);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        JsonObject output = (JsonObject) JsonParser.parseString(gson.toJson(person));
        output.addProperty("success", true);
        return output;
    }

    public static JsonObject getPeople(Request request, Response response) {
        response.type("application/json");
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int begin = parameters.get("begin").getAsInt();
            int amount = parameters.get("amount").getAsInt();
            String query = "SELECT * FROM people ORDER BY last_name COLLATE NOCASE, first_name COLLATE NOCASE LIMIT " +
                    begin + ", " + amount;
            People people = new People();
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Person person = new Person();
                populatePerson(person, rs);
                people.add(person);
            }
            JsonObject object = getJsonObject(getJson(people), true);
            conn.close();
            return object;

        } catch (SQLException | IllegalStateException e) {
            JsonObject object = getJsonObject("", false);
            object.addProperty("error", e.getMessage());
            System.out.println(e.getMessage());
            return object;

        }
    }

    public static JsonObject updatePerson(Request request, Response response) {
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        response.type("application/json");
        Person person = setPersonFromRequest(personId, parameters);
        String query = "UPDATE people SET date_of_birth = ?, first_name = ?, last_name = ?, middle_names = ?, " +
                "title = ?, nick_name = ?, maiden_name = ? WHERE person_id = ?";
        try {
            return createPersonPStmt(person, query);
        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject object = getJsonObject("{}", false);
            object.addProperty("error", e.getMessage());
            return object;
        }
    }

    public static JsonObject addPerson(Request request, Response response) {
        response.type("application/json");
        int personId = 0;
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        Person person = setPersonFromRequest(personId, parameters);
        String query = "INSERT INTO people(date_of_birth, first_name, last_name, middle_names, title, " +
                "nick_name, maiden_name, married_name) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            return createPersonPStmt(person, query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            JsonObject output = getJsonObject("", false);
            output.addProperty("error", e.getMessage());
            return output;
        }
    }

    @NotNull
    private static JsonObject createPersonPStmt(Person person, String query) throws SQLException {
        int ret_val;
        Connection conn = connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, (person.getDateOfBirth()==null)?null:getTimeStamp(person.getDateOfBirth().toString()));
        p_stmt.setString(2, person.getFirstName());
        p_stmt.setString(3, person.getLastName());
        p_stmt.setString(4, person.getMiddleNames());
        p_stmt.setString(5, person.getTitle());
        p_stmt.setString(6, person.getNickName());
        p_stmt.setString(7, person.getMaidenName());
        p_stmt.setInt(8, person.getId());
        ret_val = p_stmt.executeUpdate();
        JsonObject output = getJsonObject("{}", true);
        output.addProperty("sql_return_value", ret_val);
        conn.close();
        return output;
    }

    public static JsonObject removePerson(Request request, Response response) {
        response.type("application/json");
        JsonObject output;
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        System.out.println("request: " + request.body());
        int personId = parameters.get("personId").getAsInt();
        String query = "delete from people where person_id = ?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, personId);
            pstmt.executeUpdate();
            output = getJsonObject("{}", true);
            output.addProperty("personId", personId);
            conn.close();
        } catch (SQLException e) {
            output = getJsonObject("{}", false);
            output.addProperty("error", e.getMessage());
        }
        return output;
    }

    /**
     * Get the number of people in the database
     * @param request
     * @param response
     * @return
     */
    public static JsonObject getPersonCount(Request request, Response response) {
        response.type("application/json");
        String query = "SELECT count(*) as record_count FROM people";
        int record_count = 0;
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            record_count = rs.getInt("record_count");
            JsonObject output = getJsonObject("{}", true);
            output.addProperty("record_count", record_count);
            conn.close();
            return output;
        } catch (SQLException e) {
            JsonObject output = getJsonObject("{}", false);
            output.addProperty("error", e.getMessage());
            return output;
        }
    }

    /**
     * Populate a Person object from a ResultSet
     * @param person
     * @param rs
     * @throws SQLException
     */
    private static void populatePerson(Person person, ResultSet rs) throws SQLException {
        person.setId(rs.getInt("person_id"));
        person.setDateOfBirth(rs.getDate("date_of_birth"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setTitle(rs.getString("title"));
        person.setMaidenName(rs.getString("maiden_name"));
        person.setMiddleNames(rs.getString("middle_names"));
        person.setNickName(rs.getString("nick_name"));
    }

    /**
     * Populate Person class from response given as JsonObject
     * @param personId
     * @param parameters
     * @return
     */
    private static Person setPersonFromRequest(int personId, JsonObject parameters) {
        Person person = new Person();
        person.setId(personId);
        person.setFirstName(parameters.has("firstName") ? parameters.get("firstName").getAsString() : null);
        person.setLastName(parameters.has("lastName") ? parameters.get("lastName").getAsString() : null);
        person.setMiddleNames(parameters.has("middleNames") ? parameters.get("middleNames").getAsString() : null);
        person.setTitle(parameters.has("title") ? parameters.get("title").getAsString() : null);
        person.setNickName(parameters.has("nickName") ? parameters.get("nickName").getAsString() : null);
        person.setMaidenName(parameters.has("maidenName") ? parameters.get("maidenName").getAsString() : null);
        person.setDateOfBirth(parameters.has("dateOfBirth") ? getSQLDate(parameters.get("dateOfBirth").getAsString()) : null);
        return person;
    }

}
