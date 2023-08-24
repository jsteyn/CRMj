package com.jannetta.crmj.appj.nonhibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.appj.JPropertiesManager;
import com.jannetta.crmj.database.model.Address;
import com.jannetta.crmj.database.model.Addresses;
import com.jannetta.crmj.database.model.People;
import com.jannetta.crmj.database.model.Person;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class NonHibernateQueries {

    private static JPropertiesManager properties = new JPropertiesManager();

    public NonHibernateQueries(JPropertiesManager properties) {
        NonHibernateQueries.properties = properties;
    }

    private static int limit = 10;
    private static int offset = 0;

    private static Connection connect() {
        Connection conn = null;
        try {
            String url = properties.getProperty("database.jdbc.protocol") + properties.getProperty("database.filepath");
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


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
        int personId = parameters.get("recordId").getAsInt();
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
        int ret_val = 0;
        try {
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
            JsonObject object = getJsonObject("{}", true);
            System.out.println("Ran executeUpdate ret val: " + person.getId());
            object.addProperty("sql_ret_val", ret_val);
            conn.close();
            return object;
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
        int ret_val = 0;
        try {
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
            JsonObject output = getJsonObject("", true);
            output.addProperty("sql_return_value", ret_val);
            conn.close();
            return output;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            JsonObject output = getJsonObject("", false);
            output.addProperty("error", e.getMessage());
            return output;
        }
    }


    public static JsonObject removePerson(Request request, Response response) {
        response.type("application/json");
        JsonObject output;
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        System.out.println("Delete person_id: " + personId);
        String query = "delete from people where person_id = ?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, personId);
            pstmt.executeUpdate();
            output = getJsonObject("{}", true);
            output.addProperty("recordId", personId);
            conn.close();
        } catch (SQLException e) {
            output = getJsonObject("{}", false);
            output.addProperty("error", e.getMessage());
        }
        return output;
    }

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

    public static JsonObject getAddresses(Request request, Response response) {
        response.type("application/json");
        Addresses addresses = new Addresses();
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int personId = parameters.get("recordId").getAsInt();
            String query = "select a.address_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                    "a.country , a.county , a.postcode  from person_address as p join addresses as a where " +
                    "p.person_id='" + personId + "'";
            String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // loop through the result set

            while (rs.next()) {
                Address address = new Address();
                address.setId(rs.getInt("address_id"));
                address.setAddressLine1(rs.getString("address_line_1"));
                address.setAddressLine2(rs.getString("address_line_2"));
                address.setAddressLine3(rs.getString("address_line_3"));
                address.setCity(rs.getString("city"));
                address.setCountry(rs.getString("country"));
                address.setCounty(rs.getString("county"));
                address.setPostcode(rs.getString("postcode"));
                addresses.add(address);
            }
            String json = getJson(addresses);
            System.out.println(json);
            conn.close();
            return getJsonObject(getJson(addresses), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return getJsonObject(getJson(addresses), false);
        }
    }

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

    public static JsonObject getJsonObject(@NotNull String json, Boolean success) {
        if (json.equals("")) json = "{}";
        JsonObject output = (JsonObject) JsonParser.parseString(json);
        output.addProperty("success", success);
        return output;
    }

    public static String getJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting();
        Gson gson = gsonBuilder.create(); // Add pretty printing for easy reading
        return gson.toJson(object);
    }

    public static String getTimeStamp(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = null;
        Timestamp timestamp = null;
        try {
            parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return String.valueOf(timestamp.getTime());
        } catch (ParseException e) {
            return null;
            //throw new RuntimeException(e);
        }
    }

    public static java.sql.Date getSQLDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (date.equals("null") || date.equals("") || date == null) {
                System.out.println("In here");
                return null;
            } else {
                java.util.Date parsedDate = dateFormat.parse(date);
                java.sql.Date sql_date = new java.sql.Date(parsedDate.getTime());
                return sql_date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
            //throw new RuntimeException(e);
        }

    }


}