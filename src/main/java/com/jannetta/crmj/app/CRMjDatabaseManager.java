package com.jannetta.crmj.app;

import com.jannetta.crmj.database.Contact;
import com.jannetta.crmj.database.ContactMapper;
import com.jannetta.crmj.database.DatabaseManager;
import org.apache.ibatis.session.SqlSession;

import java.nio.file.Path;

public class CRMjDatabaseManager extends DatabaseManager {
    private ContactMapper m_contactMapper = null;

    public CRMjDatabaseManager(String driver, Path database) {
        super(driver, database);
    }

    public Contact getContact(int id) {
        return m_contactMapper.getContact(id);
    }

    @Override
    protected void onOpen(SqlSession session) {
        m_contactMapper = session.getMapper(ContactMapper.class);
    }

    @Override
    protected void onClose() {
        m_contactMapper = null;
    }
}
