package com.adobe.aem.guides.practice.core.services;

import java.sql.Connection;

public interface DatabaseService {
    Connection getConnection();
}
