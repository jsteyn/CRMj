package com.jannetta.crmj.server;

/**
 * Properties interface for server management.
 */
public interface ServerProperties {
    /**
     * @return Port in which the web server is to be hosted on.
     */
    int getPort();

    /**
     * @param port Port in which the web server is to be hosted on.
     */
    void setPort(int port);
}
