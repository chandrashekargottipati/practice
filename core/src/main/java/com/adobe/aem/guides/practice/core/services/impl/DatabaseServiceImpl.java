package com.adobe.aem.guides.practice.core.services.impl;

import com.adobe.aem.guides.practice.core.services.DatabaseService;
import com.adobe.aem.guides.practice.core.services.MySQLConfigService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component(service = DatabaseService.class, immediate = true)
public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Reference
    private MySQLConfigService mySQLConfigService;

    @Override
    public Connection getConnection() {
        try {
            Class.forName(mySQLConfigService.getDbDriver());
            return DriverManager.getConnection(
                    mySQLConfigService.getJdbcUrl(),
                    mySQLConfigService.getDbUsername(),
                    mySQLConfigService.getDbPassword()
            );
        } catch (ClassNotFoundException e) {
            LOG.error("MySQL Driver not found", e);
        } catch (SQLException e) {
            LOG.error("Error getting database connection", e);
        }
        return null;
    }
}
