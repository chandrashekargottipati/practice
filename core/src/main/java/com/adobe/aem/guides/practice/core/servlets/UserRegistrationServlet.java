//package com.adobe.aem.guides.practice.core.servlets;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.SlingHttpServletResponse;
//import org.apache.sling.api.servlets.SlingAllMethodsServlet;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Reference;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//
//@Component(
//        service = Servlet.class,
//        property = {
//                "sling.servlet.methods=POST",
//                "sling.servlet.paths=/bin/register",
//                "sling.servlet.extensions=html"
//        }
//)
//public class UserRegistrationServlet extends SlingAllMethodsServlet {
//    private static final long serialVersionUID = 1L;
//
//    @Reference
//    private DataSource dataSource;
//
//
//
//
//    @Override
//    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
//            throws ServletException, IOException {
//
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//
//        // Validate input
//        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
//            sendResponse(response, false, "Email and password are required");
//            return;
//        }
//
//        try {
//            // Register user
//            if (registerUser(email, password)) {
//                sendResponse(response, true, "User registered successfully");
//            } else {
//                sendResponse(response, false, "Registration failed");
//            }
//        } catch (Exception e) {
//            sendResponse(response, false, "Registration error: " + e.getMessage());
//        }
//    }
//
//    private boolean registerUser(String email, String password) throws Exception {
//        Connection connection = null;
//        PreparedStatement pstmt = null;
//
//        try {
//            // Establish database connection
//            connection = dataSource.getConnection();
//
//            // Hash the password
//            String hashedPassword = DigestUtils.sha256Hex(password);
//
//            // Prepare SQL statement
//            String sql = "INSERT INTO login.users (email, password) VALUES (?, ?)";
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setString(1, email);
//            pstmt.setString(2, hashedPassword);
//
//            // Execute insert
//            return pstmt.executeUpdate() > 0;
//        } finally {
//            // Close resources
//            closeResources(connection, pstmt);
//        }
//    }
//
//    private void sendResponse(SlingHttpServletResponse response, boolean success, String message)
//            throws IOException {
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(
//                "{\"success\": " + success + ", \"message\": \"" +
//                        message.replace("\"", "\\\"") + "\"}"
//        );
//        response.setStatus(success ?
//                SlingHttpServletResponse.SC_OK :
//                SlingHttpServletResponse.SC_BAD_REQUEST);
//    }
//
//    private void closeResources(Connection conn, PreparedStatement pstmt) {
//        try {
//            if (pstmt != null) pstmt.close();
//            if (conn != null) conn.close();
//        } catch (Exception e) {
//            // Log error (use proper logging in production)
//            System.err.println("Error closing database resources: " + e.getMessage());
//        }
//    }
//}