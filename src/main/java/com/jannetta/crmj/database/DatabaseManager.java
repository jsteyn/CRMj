package com.jannetta.crmj.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.LockModeType;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DatabaseManager implements Closeable {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private final GsonBuilder m_gsonBuilder;

    private final DatabaseProperties m_properties;
    private final SessionFactory m_sessionFactory;
    private Session m_session = null;

    public DatabaseManager(@NotNull DatabaseProperties properties, @NotNull Class<?>[] entities) {
        m_properties = properties;

        m_gsonBuilder = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd");

        s_LOGGER.info("Loading database at: [{}]", m_properties.getFullDatabaseUrl());

        HashMap<String, String> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", m_properties.getDatabaseDriver());
        settings.put("hibernate.connection.url", m_properties.getFullDatabaseUrl());
        settings.put("hibernate.dialect", m_properties.getDatabaseDialect());
        settings.put("hibernate.hbm2ddl.auto", "update"); // Normally update, may want create-drop
        settings.put("hibernate.show_sql", "true");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(settings).build();
        MetadataSources sources = new MetadataSources(registry);
        for (Class<?> entity : entities) {
            sources.addAnnotatedClass(entity);
        }
        Metadata metadata = sources.getMetadataBuilder().build();

        m_sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    /**
     * Open this resource, making all underlying resources available for use. If a session is already open, this
     * method will wait until it is closed before returning.
     * <br>
     * Should be used in conjunction with {@link #close()}.
     * @throws RuntimeException If the session takes more than 2000ms to unlock.
     */
    public void open() {
        long start = System.currentTimeMillis();
        while (m_session != null) {
            if (System.currentTimeMillis() > 2000)
                throw new RuntimeException("Timeout waiting for session to unlock.");
        }
        m_session = m_sessionFactory.openSession();
        m_session.beginTransaction();
    }

    @Override
    public void close() {
        m_session.getTransaction().commit();
        m_session.close();
        m_session = null;
    }

    public Gson createGson() {
        return m_gsonBuilder.create();
    }

    public <T> void create(@NotNull T object) {
        m_session.lock(object, LockModeType.PESSIMISTIC_WRITE);
        m_session.persist(object);
    }

    public <T> List<T> readAll(@NotNull Class<T> type) {
        return m_session.createQuery("from " + type.getName(), type).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
    }

    public <T> List<T> readFrom(@NotNull Class<T> type, @NotNull String where, Map<String, Object> parameters) {
        Query<T> query = m_session.createQuery(String.format("from %s where %s", type.getName(), where), type);
        if (parameters != null)
            query.setProperties(parameters);
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    public List<Object[]> read(@NotNull String rawQuery, Map<String, Object> parameters) {
        Query<Object[]> query = m_session.createQuery(rawQuery, Object[].class);
        if (parameters != null)
            query.setProperties(parameters);
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    public Object readSingle(@NotNull String rawQuery, Map<String, Object> parameters) {
        Query<Object[]> query = m_session.createQuery(rawQuery, Object[].class);
        if (parameters != null)
            query.setProperties(parameters);
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
    }

    public List<Object[]> readLimit(@NotNull String rawQuery, Map<String, Object> parameters, int limit, int offset) {
        Query<Object[]> query = m_session.createQuery(rawQuery, Object[].class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        if (parameters != null)
            query.setProperties(parameters);
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    public <T> void update(@NotNull T object) {
        m_session.lock(object, LockModeType.PESSIMISTIC_WRITE);
        m_session.merge(object);
    }

    public <T> void delete(@NotNull T object) {
        m_session.lock(object, LockModeType.PESSIMISTIC_WRITE);
        m_session.delete(object);
    }
}
