package com.adobe.aem.guides.practice.core.servlets;

import com.adobe.aem.guides.practice.core.services.DatabaseService;
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
                "sling.servlet.paths=/bin/register",
                "sling.servlet.methods=POST"
        }
)
public class RegisterServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);

    @Reference
    private DatabaseService databaseService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username == null || email == null || password == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing parameters\"}");
            return;
        }

        try (Connection conn = databaseService.getConnection()) {
            if (conn == null) {
                LOG.error("Database connection is null");
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database connection failed\"}");
                return;
            }

            String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) { // Check if count > 0
                        response.setStatus(SlingHttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("{\"error\": \"User already exists\"}");
                        return;
                    }
                }
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Error processing password\"}");
                return;
            }

            String insertUserQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, email);
                insertStmt.setString(3, hashedPassword);
                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows == 0) {
                    response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"User registration failed\"}");
                    return;
                }

                response.setStatus(SlingHttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\": \"User registered successfully\"}");
            }

        } catch (SQLException e) {
            LOG.error("Database error", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
        }
    }

    // Hash password using SHA-256
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
}
