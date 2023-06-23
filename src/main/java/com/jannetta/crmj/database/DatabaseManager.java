package com.jannetta.crmj.database;

import com.jannetta.crmj.database.model.Contact;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.*;

public abstract class DatabaseManager implements AutoCloseable {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private final SessionFactory m_sessionFactory;
    private Session m_session = null;

    public DatabaseManager(String driver, String url) {
        s_LOGGER.info("Loading database with driver: [{}] at url: [{}]", driver, url);

        Configuration configuration = new Configuration()
            .addAnnotatedClass(Contact.class)
            .setProperty("hibernate.connection.driver_class", driver)
            .setProperty("hibernate.connection.url", url)
            .setProperty("hibernate.dialect", "org.sqlite.hibernate.dialect.SQLiteDialect");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties()).build();

        MetadataSources sources = new MetadataSources(registry);
        Metadata metadata = sources.getMetadataBuilder().build();

        m_sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    /**
     * Open this resource, making all underlying resources available for use.
     * <br>
     * Should be used in conjunction with {@link #close()}.
     */
    public void open() {
        m_session = m_sessionFactory.openSession();
        m_session.beginTransaction();
        onOpen(m_session);
    }

    @Override
    public void close() {
        m_session.getTransaction().commit();
        m_session.close();
        m_session = null;
    }

    protected final Session getSession() {
        return m_session;
    }

    protected abstract void onOpen(Session session);
    protected abstract void onClose();
}
