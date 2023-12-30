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

    private static JsonObject createAddtessPStmt(Address address) throws SQLException {
        String query = "UPDATE addresses SET address_line_1 = ?, address_line_2 = ?, address_line_3 = ?, " +
                "city = ?, county = ?, country = ?, postcode = ? WHERE address_id = ?";        int ret_val;
        Connection conn = connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, address.getAddressLine1());
        p_stmt.setString(2, address.getAddressLine2());
        p_stmt.setString(3, address.getAddressLine3());
        p_stmt.setString(4, address.getCity());
        p_stmt.setString(5, address.getCounty());
        p_stmt.setString(6, address.getCountry());
        p_stmt.setString(7, address.getPostcode());
        p_stmt.setInt(8, address.getId());
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
     * Add a new address for an existing person. The body should contain the id, as an integer, for the person
     * as {"personId": [id]}
     * @param request
     * @param response
     * @return
     */
    public static JsonObject addAddress(Request request, Response response) {
        response.type("application/json");
        System.out.println(request.body());
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        // set address_id to 0 because it is a new address and the id will be autogenerated
        int address_id = 0;
        if (!(parameters.get("addressId") == null)) {
            address_id = parameters.get("addressId").getAsInt();
        }
        int person_id = parameters.get("personId").getAsInt();
        // get address info from request (html form)
        Address address = setAddressFromRequest(address_id, parameters);
        try {
            // add a new address
            if (address_id == 0) {
                JsonObject jsonObject = insertAddress(address);
                address_id = jsonObject.get("addressId").getAsInt();
            }
            // add an entry to relate address to person
            joinPersonWithAddress(address_id, person_id);
            JsonObject output = getJsonObject("", false);
            output.addProperty("success", true);
            return output;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            JsonObject output = getJsonObject("", false);
            output.addProperty("error", e.getMessage());
            return output;
        }
    }


    /**
     * Associate an address in the Addresses table with a person in the Person table (person_address table)
     * and execute the query
     * @param address_id
     * @param query
     * @return
     */
    public static JsonObject joinPersonWithAddress(int address_id, int person_id) throws SQLException {
        String query = "INSERT INTO person_address(address_id, person_id) VALUES(?, ?)";
        Connection conn = connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setInt(1, address_id);
        p_stmt.setInt(2, person_id);
        int ret_val = p_stmt.executeUpdate();
        JsonObject output = getJsonObject("{}", true);
        output.addProperty("sql_return_value", ret_val);
        conn.close();
        return output;
    }

    /**
     * Insert a new address (addresses table) into the database
     * and execute the query.
     * @param address
     * @param query
     * @return
     */
    public static JsonObject insertAddress(Address address) throws SQLException {
        String query = "INSERT INTO addresses(address_line_1, address_line_2, address_line_3, city, county, county, " +
                "postcode) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Connection conn = connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, address.getAddressLine1());
        p_stmt.setString(2, address.getAddressLine2());
        p_stmt.setString(3, address.getAddressLine3());
        p_stmt.setString(4, address.getCity());
        p_stmt.setString(5, address.getCounty());
        p_stmt.setString(6, address.getCountry());
        p_stmt.setString(7, address.getPostcode());
        int ret_val = p_stmt.executeUpdate();
        JsonObject output = getJsonObject("{}", true);
        output.addProperty("sql_return_value", ret_val);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select last_insert_rowid()");
        if (!rs.next()) {
            System.out.println("Something went wrong");
        } else {
            int address_id = rs.getInt("last_insert_rowid()");
            output.addProperty("addressId", address_id);
        }
        conn.close();
        return output;
    }

    /**
     * Request an address using its addressID which should be available in the request body as an integer in
     * the format: {"addressID: [ID]}
     * @param request
     * @param response
     * @return
     */
    public static JsonObject getAddress(Request request, Response response) {
        System.out.println(request.body());
        response.type("application/json");
        Address address = new Address();
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int addressId = parameters.get("addressId").getAsInt();
            String query = "select a.address_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                    "a.country , a.county , a.postcode  from addresses as a where " +
                    "a.address_id='" + addressId + "'";
            String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // loop through the result set
            rs.next();
            address.setId(rs.getInt("address_id"));
            address.setAddressLine1(rs.getString("address_line_1"));
            address.setAddressLine2(rs.getString("address_line_2"));
            address.setAddressLine3(rs.getString("address_line_3"));
            address.setCity(rs.getString("city"));
            address.setCountry(rs.getString("country"));
            address.setCounty(rs.getString("county"));
            address.setPostcode(rs.getString("postcode"));
            String json = getJson(address);
            conn.close();
            return getJsonObject(getJson(address), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return getJsonObject(getJson(address), false);
        }
    }

    /**
     * Find the addresses linked to a person. The request body should contain the personID as an integer in the
     * format {"personID": [ID]}
     * @param request
     * @param response
     * @return
     */
    public static JsonObject getAddresses(Request request, Response response) {
        response.type("application/json");
        Addresses addresses = new Addresses();
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int personId = parameters.get("personId").getAsInt();
            String query = "";
            if (personId == 0) {
                // return all addresses
                query = "select a.address_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                        "a.country , a.county , a.postcode  from addresses as a ";
            } else {
                // return
                query = "select a.address_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                        "a.country , a.county , a.postcode  from person_address as p join addresses as a " +
                        "on p.address_id = a.address_id where " +
                        "p.person_id='" + personId + "'";
            }
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
            conn.close();
            return getJsonObject(getJson(addresses), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return getJsonObject(getJson(addresses), false);
        }
    }

    /**
     * Populate Address from response given as JsonObject
     * @param addressId
     * @param parameters
     * @return
     */
    private static Address setAddressFromRequest(int addressId, JsonObject parameters) {
        Address address = new Address();
        address.setId(addressId);
        address.setAddressLine1(parameters.has("addressLine1") ? parameters.get("addressLine1").getAsString() : null);
        address.setAddressLine2(parameters.has("addressLine2") ? parameters.get("addressLine2").getAsString() : null);
        address.setAddressLine3(parameters.has("addressLine3") ? parameters.get("addressLine3").getAsString() : null);
        address.setCity(parameters.has("city") ? parameters.get("city").getAsString() : null);
        address.setCounty(parameters.has("county") ? parameters.get("county").getAsString() : null);
        address.setCountry(parameters.has("country") ? parameters.get("country").getAsString() : null);
        address.setPostcode(parameters.has("postcode") ? parameters.get("postcode").getAsString() : null);
        return address;
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

    public static JsonObject removeLinkedAddress(Request request, Response response) {
        response.type("application/json");
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        int addressId = parameters.get("addressId").getAsInt();
        String query = "DELETE FROM person_address WHERE person_id = ? and address_id= ?";
        Connection conn = connect();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);

            // set the corresponding param
            pstmt.setInt(1, personId);
            pstmt.setInt(2, addressId);
            // execute the delete statement
            pstmt.executeUpdate();
            conn.close();
            return getJsonObject("{}", true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Turn a JSON string into a JsonObject
     * @param json
     * @param success
     * @return
     */
    public static JsonObject getJsonObject(@NotNull String json, Boolean success) {
        if (json.equals("")) json = "{}";
        JsonObject output = (JsonObject) JsonParser.parseString(json);
        output.addProperty("success", success);
        return output;
    }

    /**
     * Get a JSON string from an object - i.e. serialise the object
     * @param object
     * @return
     */
    public static String getJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting();
        Gson gson = gsonBuilder.create(); // Add pretty printing for easy reading
        return gson.toJson(object);
    }

    /**
     * Turn a date, in the format yyyy-MM-dd, into a timestamp and return it as string.
     * @param date
     * @return
     */
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

    /**
     * Turn a string date, in the format yyyy-MM-dd, into an SQL date
     * @param date
     * @return
     */
    public static java.sql.Date getSQLDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (date.equals("null") || date.equals("") || date == null) {
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


    public static JsonObject updateAddress(Request request, Response response) {
        Connection conn = connect();

        try {
            System.out.println(request.body());
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int addressId = parameters.get("addressId").getAsInt();
            Address address = setAddressFromRequest(addressId, parameters);
            createAddtessPStmt(address);
            return getJsonObject("", true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return getJsonObject("", false);
        }
    }
}