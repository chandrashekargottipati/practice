package com.adobe.aem.guides.practice.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MySQL Database Configuration", description = "Configuration for MySQL database connection")
public @interface MySQLConfig {

    @AttributeDefinition(name = "JDBC URL", description = "JDBC URL for the MySQL database")
    String jdbcUrl() default "jdbc:mysql://localhost:3306/myshop_db";

    @AttributeDefinition(name = "Database Username", description = "Username for the MySQL database")
    String dbUsername() default "root";

    @AttributeDefinition(name = "Database Password", description = "Password for the MySQL database")
    String dbPassword() default "Dundu@003";

    @AttributeDefinition(name = "Database Driver", description = "JDBC driver class for MySQL")
    String dbDriver() default "com.mysql.cj.jdbc.Driver";
}