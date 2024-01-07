package com.jannetta.crmj.appj.nonhibernate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.database.model.Address;
import com.jannetta.crmj.database.model.Addresses;
import spark.Request;
import spark.Response;

import java.sql.*;

public class AddressQueries {


    public static JsonObject updateAddress(Request request, Response response) {
        Connection conn = NonHibernateQueries.connect();

        try {
            System.out.println(request.body());
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int addressId = parameters.get("addressId").getAsInt();
            Address address = setAddressFromRequest(addressId, parameters);
            createAddtessPStmt(address);
            return NonHibernateQueries.getJsonObject("", true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject("", false);
        }
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
            Connection conn = NonHibernateQueries.connect();
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
            String json = NonHibernateQueries.getJson(address);
            conn.close();
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(address), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(address), false);
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
            Connection conn = NonHibernateQueries.connect();
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
            String json = NonHibernateQueries.getJson(addresses);
            conn.close();
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(addresses), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(addresses), false);
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

    public static JsonObject removeLinkedAddress(Request request, Response response) {
        response.type("application/json");
        JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
        int personId = parameters.get("personId").getAsInt();
        int addressId = parameters.get("addressId").getAsInt();
        String query = "DELETE FROM person_address WHERE person_id = ? and address_id= ?";
        Connection conn = NonHibernateQueries.connect();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);

            // set the corresponding param
            pstmt.setInt(1, personId);
            pstmt.setInt(2, addressId);
            // execute the delete statement
            pstmt.executeUpdate();
            conn.close();
            return NonHibernateQueries.getJsonObject("{}", true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static JsonObject createAddtessPStmt(Address address) throws SQLException {
        String query = "UPDATE addresses SET address_line_1 = ?, address_line_2 = ?, address_line_3 = ?, " +
                "city = ?, county = ?, country = ?, postcode = ? WHERE address_id = ?";
        int ret_val;
        Connection conn = NonHibernateQueries.connect();
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
        JsonObject output = NonHibernateQueries.getJsonObject("{}", true);
        output.addProperty("sql_return_value", ret_val);
        conn.close();
        return output;
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
            JsonObject output = NonHibernateQueries.getJsonObject("", false);
            output.addProperty("success", true);
            return output;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.status(500);
            JsonObject output = NonHibernateQueries.getJsonObject("", false);
            output.addProperty("error", e.getMessage());
            return output;
        }
    }


    /**
     * Associate an address in the Addresses table with a person in the Person table (person_address table)
     * and execute the query
     * @param address_id
     * @param person_id
     * @return
     */
    public static JsonObject joinPersonWithAddress(int address_id, int person_id) throws SQLException {
        String query = "INSERT INTO person_address(address_id, person_id) VALUES(?, ?)";
        Connection conn = NonHibernateQueries.connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setInt(1, address_id);
        p_stmt.setInt(2, person_id);
        int ret_val = p_stmt.executeUpdate();
        JsonObject output = NonHibernateQueries.getJsonObject("{}", true);
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
        Connection conn = NonHibernateQueries.connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, address.getAddressLine1());
        p_stmt.setString(2, address.getAddressLine2());
        p_stmt.setString(3, address.getAddressLine3());
        p_stmt.setString(4, address.getCity());
        p_stmt.setString(5, address.getCounty());
        p_stmt.setString(6, address.getCountry());
        p_stmt.setString(7, address.getPostcode());
        int ret_val = p_stmt.executeUpdate();
        JsonObject output = NonHibernateQueries.getJsonObject("{}", true);
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
}
