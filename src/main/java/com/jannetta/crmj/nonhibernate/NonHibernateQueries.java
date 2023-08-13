package com.jannetta.crmj.nonhibernate;

import java.sql.*;

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

    public static String getAddresses(int personID) {
        String query = "select person_id, a.address_line_1, a.address_line_2, a.address_line_3 , a.city , " +
                "a.country , a.county , a.postcode  from person_address as p join addresses as a where " +
                "p.person_id='" + personID + "'";
        System.out.println(query);
        String url = "jdbc:sqlite:" + "/home/jannetta/.CRMj/data.db";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // loop through the result set
            while (rs.next()) {

                System.out.println(rs.getInt("person_id") + "\t" +
                        rs.getString("addressLine1") + "\t" +
                        rs.getDouble("city"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }


}
