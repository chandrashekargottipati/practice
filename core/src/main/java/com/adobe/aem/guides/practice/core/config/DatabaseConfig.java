package com.adobe.aem.guides.practice.core.config;

public class DatabaseConfig {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClass;

    public DatabaseConfig(String jdbcUrl, String username, String password, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverClass() {
        return driverClass;
    }
}
