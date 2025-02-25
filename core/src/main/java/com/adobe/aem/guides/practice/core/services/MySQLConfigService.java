package com.adobe.aem.guides.practice.core.services;

public interface MySQLConfigService {
    String getJdbcUrl();
    String getDbUsername();
    String getDbPassword();
    String getDbDriver();
}
