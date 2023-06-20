package com.jannetta.crmj.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jannetta.crmj.datamodel.Contact;

import java.util.UUID;


public class ContactDao {

    /**
     * Find a contact using its email address and return as a JSON string
     * @param userEmail
     * @return
     */
    public static String getJSONContact(String userEmail) {
        Contact contact = new Contact(UUID.randomUUID().toString(),
                "Jannetta","Sophia","Steyn"); //DataController.getCoreRecords().get(siteNo);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(contact, Contact.class).toString();
    }

    public static String getJSONUsername(String userEmail) {
        String uuid = UUID.randomUUID().toString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return "{username: \"" + uuid + "\"}";
    }

    public static String getUsername(String email) {
        Contact contact = new Contact(UUID.randomUUID().toString(),
                "Jannetta","Sophia","Steyn"); //DataController.getCoreRecords().get(siteNo);
        return contact.getUUID().toString();
    }
}



