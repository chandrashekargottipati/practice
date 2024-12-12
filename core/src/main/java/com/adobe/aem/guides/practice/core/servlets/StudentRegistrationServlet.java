package com.adobe.aem.guides.practice.core.servlets;

import com.adobe.aem.guides.practice.core.config.DatabaseConfig;
import com.adobe.aem.guides.practice.core.config.DatabaseConfigReader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/studentRegister",  // Adjusted path
                "sling.servlet.extensions=json"
        }
)
public class StudentRegistrationServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(StudentRegistrationServlet.class);

    @Reference
    private DatabaseConfigReader databaseConfigReader;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            sendResponse(response, false, "Email and password are required");
            return;
        }

        try {
            if (registerStudent(email, password)) {
                sendResponse(response, true, "Student registered successfully");
            } else {
                sendResponse(response, false, "Student registration failed");
            }
        } catch (Exception e) {
            log.error("Error registering student: ", e);
            sendResponse(response, false, "Error: " + e.getMessage());
        }
    }

    private boolean registerStudent(String email, String password) throws Exception {
        DatabaseConfig config = databaseConfigReader.getDatabaseConfig();

        if (config == null) {
            throw new Exception("Database configuration not found");
        }

        log.info("JDBC URL: {}", config.getJdbcUrl());
        log.info("Username: {}", config.getUsername());
        log.info("Driver Class: {}", config.getDriverClass());

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            // Load JDBC driver class dynamically using the property
            if (config.getDriverClass() != null && !config.getDriverClass().isEmpty()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new Exception("JDBC Driver not found: " + e.getMessage());
                }
            }

            // Establish database connection using JDBC URL, username, and password from config
            connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());

            // Hash the password
            String hashedPassword = DigestUtils.sha256Hex(password);

            // Use your specific table and column names
            String sql = "INSERT INTO login.users (email, password) VALUES (?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);

            // Execute insert and return success status
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                throw new SQLException("Failed to register student");
            }
        } finally {
            // Close resources to prevent memory leaks
            closeResources(connection, pstmt);
        }
    }

    private void sendResponse(SlingHttpServletResponse response, boolean success, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"success\": " + success + ", \"message\": \"" +
                        message.replace("\"", "\\\"") + "\"}"
        );
        response.setStatus(success ?
                SlingHttpServletResponse.SC_OK :
                SlingHttpServletResponse.SC_BAD_REQUEST);
    }

    private void closeResources(Connection connection, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            log.error("Error closing resources", e);
        }
    }
}
