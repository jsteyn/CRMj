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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    protected static Connection connect() {
        Connection conn = null;
        try {
            String url = properties.getProperty("database.jdbc.protocol") + properties.getProperty("database.filepath");
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Turn a JSON string into a JsonObject
     *
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
     *
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
     *
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
     *
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
}