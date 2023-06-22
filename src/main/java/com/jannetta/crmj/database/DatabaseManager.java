package com.jannetta.crmj.database;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class DatabaseManager implements AutoCloseable {
    private static final Logger s_LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private final SqlSessionFactory m_sessionFactory;
    private SqlSession m_session = null;

    public DatabaseManager(String driver, String url) {
        s_LOGGER.info("Loading database with driver: [{}] at url: [{}]", driver, url);
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl(url);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ContactMapper.class);

        m_sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * Open this resource, making all underlying resources available for use.
     * <br>
     * Should be used in conjunction with {@link #close()}, or managed by a try-with-resources statement.
     */
    public void open() {
        m_session = m_sessionFactory.openSession();
        onOpen(m_session);
    }

    @Override
    public void close() throws Exception {
        m_session.commit();
        m_session.close();
        m_session = null;
    }

    protected abstract void onOpen(SqlSession session);
    protected abstract void onClose();
}
