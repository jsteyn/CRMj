package com.jannetta.crmj.database;

import java.nio.file.Path;

/**
 * Properties interface for database management.
 */
public interface DatabaseProperties {
    /**
     * @return JDBC driver to be used by Hibernate.
     */
    public String getDatabaseDriver();

    /**
     * @param databaseDriver JDBC driver to be used by Hibernate.
     */
    public void setDatabaseDriver(String databaseDriver);

    /**
     * @return Filepath or URL where the database can be interfaced with.
     */
    public Path getDatabasePath();

    /**
     * @param path Filepath or URL where the database can be interfaced with.
     */
    public void setDatabasePath(Path path);

    /**
     * @return JDBC protocol (and sub-protocol) to be used to connect to the database.
     */
    public String getDatabaseProtocol();

    /**
     * @param protocol JDBC protocol (and sub-protocol) to be used to connect to the database.
     */
    public void setDatabaseProtocol(String protocol);

    /**
     * @return Database dialect to be used by Hibernate for query generation.
     */
    public String getDatabaseDialect();

    /**
     * @param protocol Database dialect to be used by Hibernate for query generation.
     */
    public void setDatabaseDialect(String protocol);

    /**
     * @return Fully formed database connection URL. Usually some sensible combination of the protocol and path.
     */
    public String getFullDatabaseUrl();
}
