package com.jannetta.crmj.appj.nonhibernate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jannetta.crmj.database.model.Address;
import com.jannetta.crmj.database.model.Addresses;
import com.jannetta.crmj.database.model.Email;
import com.jannetta.crmj.database.model.EmailAddresses;
import spark.Request;
import spark.Response;

import java.sql.*;

public class EmailQueries {


    public static JsonObject updateEmail(Request request, Response response) {
        Connection conn = NonHibernateQueries.connect();

        try {
            System.out.println(request.body());
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int emailId = parameters.get("emailId").getAsInt();
            Email email = setEmailFromRequest(emailId, parameters);
            createEmailPStmt(email);
            return NonHibernateQueries.getJsonObject("", true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject("", false);
        }
    }


    /**
     * Request an email address using its emailID which should be available in the request body as an integer in
     * the format: {"emailID: [ID]}
     * @param request
     * @param response
     * @return
     */
    public static JsonObject getEmail(Request request, Response response) {
        System.out.println(request.body());
        response.type("application/json");
        Address address = new Address();
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int emailId = parameters.get("addressId").getAsInt();
            String query = "select a.email_id, a.email from emailaddresses as a where " +
                    "a.email_id='" + emailId + "'";
            String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
            Connection conn = NonHibernateQueries.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // loop through the result set
            rs.next();
            address.setId(rs.getInt("email_id"));
            address.setAddressLine1(rs.getString("email"));
            String json = NonHibernateQueries.getJson(address);
            conn.close();
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(address), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(address), false);
        }
    }

    /**
     * Find the email addresses linked to a person. The request body should contain the personID as an integer in the
     * format {"personID": [ID]}
     * @param request
     * @param response
     * @return
     */
    public static JsonObject getEmails(Request request, Response response) {
        response.type("application/json");
        EmailAddresses emailaddresses = new EmailAddresses();
        try {
            JsonObject parameters = JsonParser.parseString(request.body()).getAsJsonObject();
            int personId = parameters.get("personId").getAsInt();
            String query = "";
            if (personId == 0) {
                // return all addresses
                query = "select a.email_id, a.email from emailaddresses as a ";
            } else {
                // return
                query = "select a.email_id, a.email from person_address as p join emailaddresses as a " +
                        "on p.email_id = a.email_id where " +
                        "p.person_id='" + personId + "'";
            }
            String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
            Connection conn = NonHibernateQueries.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            // loop through the result set
            while (rs.next()) {
                Email email = new Email();
                email.setId(rs.getInt("email_id"));
                email.setEmail(rs.getString("email"));
                emailaddresses.add(email);
            }
            String json = NonHibernateQueries.getJson(emailaddresses);
            conn.close();
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(emailaddresses), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return NonHibernateQueries.getJsonObject(NonHibernateQueries.getJson(emailaddresses
            ), false);
        }
    }

    /**
     * Populate Address from response given as JsonObject
     * @param emailId
     * @param parameters
     * @return
     */
    private static Email setEmailFromRequest(int emailId, JsonObject parameters) {
        Email email = new Email();
        email.setId(emailId);
        email.setEmail(parameters.has("email") ? parameters.get("email").getAsString() : null);
        return email;
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


    private static JsonObject createEmailPStmt(Email email) throws SQLException {
        String query = "UPDATE email SET email = ? WHERE email_id = ?";
        int ret_val;
        Connection conn = NonHibernateQueries.connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, email.getEmail());
        p_stmt.setInt(2, email.getId());
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
        int email_id = 0;
        if (!(parameters.get("emailId") == null)) {
            email_id = parameters.get("emailId").getAsInt();
        }
        int person_id = parameters.get("personId").getAsInt();
        // get address info from request (html form)
        Email email = setEmailFromRequest(email_id, parameters);
        try {
            // add a new address
            if (email_id == 0) {
                JsonObject jsonObject = insertEmail(email);
                email_id = jsonObject.get("emailId").getAsInt();
            }
            // add an entry to relate address to person
            joinPersonWithEmail(email_id, person_id);
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
     * @param email_id
     * @param person_id
     * @return
     */
    public static JsonObject joinPersonWithEmail(int email_id, int person_id) throws SQLException {
        String query = "INSERT INTO person_email(email_id, person_id) VALUES(?, ?)";
        Connection conn = NonHibernateQueries.connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setInt(1, email_id);
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
     * @param email
     * @return
     */
    public static JsonObject insertEmail(Email email) throws SQLException {
        String query = "INSERT INTO emails(address_line_1, address_line_2, address_line_3, city, county, county, " +
                "postcode) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Connection conn = NonHibernateQueries.connect();
        PreparedStatement p_stmt = conn.prepareStatement(query);
        p_stmt.setString(1, email.getEmail());
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
