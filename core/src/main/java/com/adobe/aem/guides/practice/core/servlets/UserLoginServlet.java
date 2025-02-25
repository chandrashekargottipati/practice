package com.adobe.aem.guides.practice.core.servlets;

import com.adobe.aem.guides.practice.core.services.MySQLConfigService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/user/authenticate",
                "sling.servlet.methods=POST"
        }
)
public class UserLoginServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UserLoginServlet.class);

    @Reference
    private MySQLConfigService mySQLConfigService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Ensure at least username or email is provided, and password is required
        if ((username == null && email == null) || password == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing username/email or password\"}");
            return;
        }

        try (Connection conn = getConnection()) {
            if (conn == null) {
                LOG.error("Database connection is null");
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database connection failed\"}");
                return;
            }

            String query = "SELECT password FROM myshop_db.users WHERE (username = ? OR ? IS NULL) OR (email = ? OR ? IS NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username != null && !username.isEmpty() ? username : null);
                stmt.setString(2, username != null && !username.isEmpty() ? username : null);
                stmt.setString(3, email != null && !email.isEmpty() ? email : null);
                stmt.setString(4, email != null && !email.isEmpty() ? email : null);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHashedPassword = rs.getString("password");
                        if (storedHashedPassword.equals(hashPassword(password))) {
                            response.setStatus(SlingHttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Login successful\", \"redirect\": \"/content/practice/us/en/home.html\"}");
                            return;
                        }
                    }
                }
            }

            response.setStatus(SlingHttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid username/email or password\"}");

        } catch (SQLException e) {
            LOG.error("Database error", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error hashing password", e);
            return null;
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName(mySQLConfigService.getDbDriver());
            return DriverManager.getConnection(
                    mySQLConfigService.getJdbcUrl(),
                    mySQLConfigService.getDbUsername(),
                    mySQLConfigService.getDbPassword());
        } catch (ClassNotFoundException e) {
            LOG.error("Database driver not found", e);
            return null;
        }
    }
}