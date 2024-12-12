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
import java.sql.*;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/loginuser"
        }
)
public class LoginServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

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
            String authenticationResult = authenticateUser(email, password);

            if ("success".equals(authenticationResult)) {
                response.sendRedirect(request.getContextPath() + "/content/practice/us/en/homepage.html");
            } else {
                sendResponse(response, false, authenticationResult); // Return specific error message
            }
        } catch (Exception e) {
            log.error("Error during login: ", e);
            sendResponse(response, false, "Error: " + e.getMessage());
        }
    }

    private String authenticateUser(String email, String password) throws Exception {
        DatabaseConfig config = databaseConfigReader.getDatabaseConfig();

        if (config == null) {
            throw new Exception("Database configuration not found");
        }

        log.info("JDBC URL: {}", config.getJdbcUrl());
        log.info("Username: {}", config.getUsername());
        log.info("Driver Class: {}", config.getDriverClass());

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            // Load JDBC driver class dynamically
            if (config.getDriverClass() != null && !config.getDriverClass().isEmpty()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new Exception("JDBC Driver not found: " + e.getMessage());
                }
            }

            // Establish database connection
            connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());

            // Fetch stored password hash
            String sql = "SELECT password FROM login.users WHERE email = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

                // Hash the input password
                String inputHashedPassword = DigestUtils.sha256Hex(password);

                // Compare passwords
                if (storedHashedPassword.equals(inputHashedPassword)) {
                    return "success";
                } else {
                    return "Incorrect password";
                }
            } else {
                return "Email not found";
            }
        } finally {
            closeResources(connection, pstmt, resultSet);
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
        response.setStatus(success ? SlingHttpServletResponse.SC_OK : SlingHttpServletResponse.SC_BAD_REQUEST);
    }

    private void closeResources(Connection connection, PreparedStatement pstmt, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            log.error("Error closing resources", e);
        }
    }
}
