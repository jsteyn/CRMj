package com.jannetta.crmj.app;

import com.jannetta.crmj.database.model.Contact;
import com.jannetta.crmj.database.DatabaseManager;
import org.hibernate.Session;

import java.util.List;

public class CRMjDatabaseManager extends DatabaseManager {
    public CRMjDatabaseManager(String driver, String url, String dialect) {
        super(driver, url, dialect);
    }

    public void insert(Contact contact) {
        getSession().persist(contact);
    }

    public List<Contact> getAllContacts() {
        return getSession().createQuery("from Contact", Contact.class).list();
    }

    public Contact getContact(int id) {
        return getSession().get(Contact.class, id);
    }

    @Override
    protected void onOpen(Session session) {

    }

    @Override
    protected void onClose() {

    }
}
