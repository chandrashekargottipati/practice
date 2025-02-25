package com.adobe.aem.guides.practice.core.services.impl;

import com.adobe.aem.guides.practice.core.config.MySQLConfig;
import com.adobe.aem.guides.practice.core.services.MySQLConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MySQLConfigService.class, immediate = true)
@Designate(ocd = MySQLConfig.class)
public class MySQLConfigServiceImpl implements MySQLConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLConfigServiceImpl.class);

    private String jdbcUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    @Activate
    protected void activate(MySQLConfig config) {
        this.jdbcUrl = config.jdbcUrl();
        this.dbUsername = config.dbUsername();
        this.dbPassword = config.dbPassword();
        this.dbDriver = config.dbDriver();

        LOG.info("MySQL Config Activated - JDBC URL: {}, Username: {}, Driver: {}", 
                jdbcUrl, dbUsername, dbDriver);
    }

    @Override
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public String getDbUsername() {
        return dbUsername;
    }

    @Override
    public String getDbPassword() {
        return dbPassword;
    }

    @Override
    public String getDbDriver() {
        return dbDriver;
    }
}
