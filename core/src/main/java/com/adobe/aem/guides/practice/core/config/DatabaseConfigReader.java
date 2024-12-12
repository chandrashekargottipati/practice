package com.adobe.aem.guides.practice.core.config;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.Configuration;

import java.util.Dictionary;

@Component(service = DatabaseConfigReader.class)
public class DatabaseConfigReader {

    @Reference
    private ConfigurationAdmin configAdmin;

    // Default database configuration
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public DatabaseConfig getDatabaseConfig() {
        try {
            Configuration config = configAdmin.getConfiguration("com.day.commons.datasource.jdbcpool.JdbcPoolService.4d9472cc-65e9-4281-9d78-b0a3977fc629");
            Dictionary<String, Object> properties = config.getProperties();
            if (properties != null) {
                String jdbcUrl = (String) properties.get("jdbc.connection.uri");
                String username = (String) properties.get("jdbc.username");
                String password = (String) properties.get("jdbc.password");
                String driverClass = (String) properties.get("jdbc.driver.class");

                return new DatabaseConfig(
                        jdbcUrl != null ? jdbcUrl : DEFAULT_JDBC_URL,
                        username != null ? username : DEFAULT_USERNAME,
                        password != null ? password : DEFAULT_PASSWORD,
                        driverClass != null ? driverClass : DEFAULT_DRIVER_CLASS
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return default configuration if no properties are found
        return new DatabaseConfig(DEFAULT_JDBC_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_DRIVER_CLASS);
    }
}
