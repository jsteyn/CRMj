package com.jannetta.crmj.nonhibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jannetta.crmj.database.model.Address;
import com.jannetta.crmj.database.model.Person;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NonHibernateQueries {

    static ReadProperties properties = ReadProperties.getInstance();

    private static Connection connect() {
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        Connection conn = null;
        try {
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

}
