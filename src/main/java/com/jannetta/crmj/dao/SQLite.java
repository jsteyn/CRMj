package com.jannetta.crmj.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SQLite {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(SQLite.class);
    static Connection conn = null;
    /**
     * Connect to a sample database
     */
    public static Connection connect() {
        String url = "jdbc:sqlite:data/contacts.db";

        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
            } else {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * select all rows in the contact table
     */
    public static void selectAll() {
        String sql = "SELECT * FROM contact";

        try {
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("UUID") + "\t" +
                        rs.getString("firstname") + "\t" +
                        rs.getString("middlename") + "\t" +
                        rs.getString("lastname"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

        // TODO: Either remove or update functionality
    /**
     * Retrieve uuid given an email address from the database
     * @param email address
     * @return UUID
     */
    public static String getUsername(String email) {
        String sql = "select uuid from contact where email=\"" + email +   "\"";
        return "";
    }

    // TODO: Remove
    public static void main(String[] args) {
        connect();
        selectAll();
    }
}
