package com.jannetta.crmj.dao;

import java.sql.*;

public class SQLite {

    /**
     * Connect to a sample database
     */
    public static Connection connect() {
        String url = "jdbc:sqlite:sqlitedb";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * select all rows in the warehouses table
     */
    public void selectAll() {
        String sql = "SELECT * FROM contacts";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

    public static String getUsername(String email) {
        String sql =
    }
}
