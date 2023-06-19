package com.jannetta.crmj.controller;

import com.jannetta.crmj.dao.ContactDao;
import com.jannetta.crmj.datamodel.Contact;
import com.jannetta.crmj.template.ViewUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContactController {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(ContactController.class);

    public static Route getContact = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/contact.vm");
    };

    public static String addContact(Request request, Response response)  {
        Map<String, Object> model = new HashMap<>();
        Contact newContact = new Contact(request.queryParams("UUID"), request.queryParams("firstname"), request.queryParams("middlename"),
                request.queryParams("lastname"));
        model.put(UUID.randomUUID().toString(), newContact);

        return ViewUtil.render(request, model, "/velocity/contact_added.vm");
    }

    public static String getUsername(String email) {
        return ContactDao.getUsername(email);
    }
    public static String getJSONUsername(String email) {
        return ContactDao.getJSONUsername(email);
    }

    public static String getJSONContact(String email) {
        return ContactDao.getJSONContact(email);
    }



}
