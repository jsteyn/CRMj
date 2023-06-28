package com.jannetta.crmj.database;

import java.nio.file.Path;

public interface DatabaseProperties {
    public String getDatabaseDriver();
    public void setDatabaseDriver(String databaseDriver);

    public Path getDatabasePath();
    public void setDatabasePath(Path path);

    public String getDatabaseProtocol();
    public void setDatabaseProtocol(String protocol);

    public String getDatabaseDialect();
    public void setDatabaseDialect(String protocol);

    public String getFullDatabaseUrl();
}
