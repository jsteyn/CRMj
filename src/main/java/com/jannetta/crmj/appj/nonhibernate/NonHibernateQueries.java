package com.jannetta.crmj.appj.nonhibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.appj.JPropertiesManager;
import com.jannetta.crmj.database.model.Address;
import com.jannetta.crmj.database.model.People;
import com.jannetta.crmj.database.model.Person;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NonHibernateQueries {

    static JPropertiesManager properties = new JPropertiesManager();

    public NonHibernateQueries(JPropertiesManager properties) {
        this.properties = properties;
    }

    private static Connection connect() {
        Connection conn = null;
        try {
            String url = properties.getProperty("database.jdbc.protocol") + properties.getProperty("database.filepath");
            System.out.println("URL: " + url);
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static List getAddresses(int personID) {
        String query = "select a.address_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                "a.country , a.county , a.postcode  from person_address as p join addresses as a where " +
                "p.person_id='" + personID + "'";
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        List<Address> addresses = new ArrayList();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return addresses;
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
                person.setId(rs.getInt("person_id"));
                person.setDateOfBirth(rs.getDate("date_of_birth"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                person.setTitle(rs.getString("title"));
                person.setMaidenName(rs.getString("maiden_name"));
                person.setMiddleNames(rs.getString("middle_names"));
                person.setNickName(rs.getString("nick_name"));
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
        String query = "select person_id, date_of_birth, first_name, last_name, middle_names, title," +
                "nick_name, maiden_name, married_name from people where person_id " + "= '" + personId +
                "' order by last_name";
        System.out.println(query);
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        Person person = new Person();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            // loop through the result set
            while (rs.next()) {
                person.setId(rs.getInt("person_id"));
                String dob = rs.getString("date_of_birth");
                if (!(dob == null || dob.equals(""))) {
                    java.sql.Date date = new java.sql.Date(Long.valueOf(dob));
                    person.setDateOfBirth(date);
                }
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                person.setTitle(rs.getString("title"));
                person.setMaidenName(rs.getString("maiden_name"));
                person.setMiddleNames(rs.getString("middle_names"));
                person.setNickName(rs.getString("nick_name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        JsonObject output = (JsonObject) JsonParser.parseString(gson.toJson(person));
        System.out.println(output.toString());
        output.addProperty("success", true);
        return output;
    }

    public static String getPeople(Request request, Response response) {
        response.type("application/json");
        String query = "select * from people order by last_name, first_name";
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        People people = new People();
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // loop through the result set
            while (rs.next()) {
                Person person = new Person();
                person.setId(rs.getInt("person_id"));
                person.setDateOfBirth(rs.getDate("date_of_birth"));
                person.setFirstName(rs.getString("first_name"));
                person.setLastName(rs.getString("last_name"));
                person.setTitle(rs.getString("title"));
                person.setMaidenName(rs.getString("maiden_name"));
                person.setMiddleNames(rs.getString("middle_names"));
                person.setNickName(rs.getString("nick_name"));
                people.add(person);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return getJson(people);
    }

    public static String getJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting();
        Gson gson = gsonBuilder.create(); // Add pretty printing for easy reading
        return gson.toJson(object);
    }

    public static Object updatePerson(Request request, Response response) {
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        response.type("application/json");
        String firstName = parameters.has("firstName") ? parameters.get("firstName").getAsString() : null;
        String lastName = parameters.has("lastName") ? parameters.get("lastName").getAsString() : null;
        String middleNames = parameters.has("middleNames") ? parameters.get("middleNames").getAsString() : null;
        String title = parameters.has("title") ? parameters.get("title").getAsString() : null;
        String nickName = parameters.has("nickName") ? parameters.get("nickName").getAsString() : null;
        String maidenName = parameters.has("maidenName") ? parameters.get("maidenName").getAsString() : null;
        String marriedName = parameters.has("marriedName") ? parameters.get("marriedName").getAsString() : null;
        String dateOfBirth = parameters.has("dateOfBirth") ? parameters.get("dateOfBirth").getAsString() : null;
        String query = "UPDATE people SET date_of_birth = ?, first_name = ?, last_name = ?, middle_names = ?, " +
                "title = ?, nick_name = ?, maiden_name = ?, married_name = ? WHERE person_id = ?";
        int ret_val = 0;
        try (Connection conn = connect();
             PreparedStatement p_stmt = conn.prepareStatement(query)) {
            System.out.println("Update " + firstName);
            p_stmt.setString(1, dateOfBirth);
            p_stmt.setString(2, firstName);
            p_stmt.setString(3, lastName);
            p_stmt.setString(4, middleNames);
            p_stmt.setString(5, title);
            p_stmt.setString(6, nickName);
            p_stmt.setString(7, maidenName);
            p_stmt.setString(8, marriedName);
            ret_val = p_stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            return "Error adding person to the database.";
        }

        // Return something meaningful here if needed
        return "{\"retval\": " + ret_val + ",\"success\": true}";
    }

    public static Object addPerson(Request request, Response response) {
        response.type("application/json");
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        String firstName = parameters.has("firstName") ? parameters.get("firstName").getAsString() : null;
        String lastName = parameters.has("lastName") ? parameters.get("lastName").getAsString() : null;
        String middleNames = parameters.has("middleNames") ? parameters.get("middleNames").getAsString() : null;
        String title = parameters.has("title") ? parameters.get("title").getAsString() : null;
        String nickName = parameters.has("nickName") ? parameters.get("nickName").getAsString() : null;
        String maidenName = parameters.has("maidenName") ? parameters.get("maidenName").getAsString() : null;
        String marriedName = parameters.has("marriedName") ? parameters.get("marriedName").getAsString() : null;
        String dateOfBirth = parameters.has("dateOfBirth") ? parameters.get("dateOfBirth").getAsString() : null;
        String query = "INSERT INTO people(date_of_birth, first_name, last_name, middle_names, title, " +
                "nick_name, maiden_name, married_name) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement p_stmt = conn.prepareStatement(query)) {
            // Assuming person_id is auto-generated or set elsewhere
            System.out.println("Insert " + firstName);
            p_stmt.setString(1, dateOfBirth);
            p_stmt.setString(2, firstName);
            p_stmt.setString(3, lastName);
            p_stmt.setString(4, middleNames);
            p_stmt.setString(5, title);
            p_stmt.setString(6, nickName);
            p_stmt.setString(7, maidenName);
            p_stmt.setString(8, marriedName);
            p_stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            return "Error adding person to the database.";
        }

        // Return something meaningful here if needed
        return "{\"success\": true}";
    }


    public static JsonObject removePerson(Request request, Response response) {
        response.type("application/json");
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        String query = "delete from people where person_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // set the corresponding param
            pstmt.setInt(1, personId);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonObject output = (JsonObject) JsonParser.parseString("{}");
        output.addProperty("recordId", personId);
        output.addProperty("success", true);
        return output;
    }

}