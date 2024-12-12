//package com.adobe.aem.guides.practice.core.servlets;
//
//import com.adobe.aem.guides.practice.core.config.DatabaseConfig;
//import com.adobe.aem.guides.practice.core.config.DatabaseConfigReader;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.SlingHttpServletResponse;
//import org.apache.sling.api.servlets.SlingAllMethodsServlet;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Reference;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//@Component(
//        service = Servlet.class,
//        property = {
//                "sling.servlet.methods=POST",
//                "sling.servlet.paths=/bin/manageStudent",
//                "sling.servlet.extensions=json"
//        }
//)
//public class StudentManagementServlet extends SlingAllMethodsServlet {
//
//    private static final Logger log = LoggerFactory.getLogger(StudentManagementServlet.class);
//
//    @Reference
//    private DatabaseConfigReader databaseConfigReader;
//
//    @Override
//    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
//            throws ServletException, IOException {
//
//        String action = request.getParameter("action"); // Determine the action
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//        String newEmail = request.getParameter("newEmail"); // For update
//        String newPassword = request.getParameter("newPassword"); // For update
//
//        try {
//            switch (action) {
//                case "register":
//                    if (email != null && password != null && registerStudent(email, password)) {
//                        sendResponse(response, true, "Student registered successfully");
//                    } else {
//                        sendResponse(response, false, "Student registration failed");
//                    }
//                    break;
//                case "update":
//                    if (email != null && (newEmail != null || newPassword != null) && updateStudent(email, newEmail, newPassword)) {
//                        sendResponse(response, true, "Student updated successfully");
//                    } else {
//                        sendResponse(response, false, "Failed to update student details");
//                    }
//                    break;
//                case "delete":
//                    if (email != null && deleteStudent(email)) {
//                        sendResponse(response, true, "Student deleted successfully");
//                    } else {
//                        sendResponse(response, false, "Failed to delete student");
//                    }
//                    break;
//                default:
//                    sendResponse(response, false, "Invalid action");
//            }
//        } catch (Exception e) {
//            log.error("Error managing student: ", e);
//            sendResponse(response, false, "Error: " + e.getMessage());
//        }
//    }
//
//    private boolean registerStudent(String email, String password) throws Exception {
//        return executeUpdate("INSERT INTO login.users (email, password) VALUES (?, ?)", email, DigestUtils.sha256Hex(password));
//    }
//
//    private boolean updateStudent(String email, String newEmail, String newPassword) throws Exception {
//        StringBuilder sql = new StringBuilder("UPDATE login.users SET ");
//        if (newEmail != null) sql.append("email = ?, ");
//        if (newPassword != null) sql.append("password = ?, ");
//        sql.setLength(sql.length() - 2); // Remove trailing comma
//        sql.append(" WHERE email = ?");
//
//        return executeUpdate(sql.toString(), newEmail, newPassword != null ? DigestUtils.sha256Hex(newPassword) : null, email);
//    }
//
//    private boolean deleteStudent(String email) throws Exception {
//        return executeUpdate("DELETE FROM login.users WHERE email = ?", email);
//    }
//
//    private boolean executeUpdate(String sql, Object... params) throws Exception {
//        DatabaseConfig config = databaseConfigReader.getDatabaseConfig();
//
//        if (config == null) {
//            throw new Exception("Database configuration not found");
//        }
//
//        try (Connection connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
//             PreparedStatement pstmt = connection.prepareStatement(sql)) {
//
//            int index = 1;
//            for (Object param : params) {
//                if (param != null) {
//                    pstmt.setObject(index++, param);
//                }
//            }
//
//            return pstmt.executeUpdate() > 0;
//        }
//    }
//
//    private void sendResponse(SlingHttpServletResponse response, boolean success, String message) throws IOException {
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(
//                "{\"success\": " + success + ", \"message\": \"" +
//                        message.replace("\"", "\\\"") + "\"}"
//        );
//        response.setStatus(success ? SlingHttpServletResponse.SC_OK : SlingHttpServletResponse.SC_BAD_REQUEST);
//    }
//}



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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/manageStudent",
                "sling.servlet.extensions=json"
        }
)
public class StudentManagementServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(StudentManagementServlet.class);

    @Reference
    private DatabaseConfigReader databaseConfigReader;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action"); // Determine the action type (update or delete)
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            switch (action) {
                case "update":
                    if (email != null && newPassword != null && newPassword.equals(confirmPassword)) {
                        if (updateStudent(email, newPassword)) {
                            sendResponse(response, true, "Account updated successfully.");
                        } else {
                            sendResponse(response, false, "Failed to update account.");
                        }
                    } else {
                        sendResponse(response, false, "Passwords do not match or missing data.");
                    }
                    break;
                case "delete":
                    if (email != null && deleteStudent(email)) {
                        sendResponse(response, true, "Account deleted successfully.");
                    } else {
                        sendResponse(response, false, "Failed to delete account.");
                    }
                    break;
                default:
                    sendResponse(response, false, "Invalid action.");
            }
        } catch (Exception e) {
            log.error("Error managing student: ", e);
            sendResponse(response, false, "Error: " + e.getMessage());
        }
    }

    private boolean updateStudent(String email, String newPassword) throws SQLException {
        String sql = "UPDATE login.users SET password = ? WHERE email = ?";
        return executeUpdate(sql, DigestUtils.sha256Hex(newPassword), email);
    }

    private boolean deleteStudent(String email) throws SQLException {
        String sql = "DELETE FROM login.users WHERE email = ?";
        return executeUpdate(sql, email);
    }

    private boolean executeUpdate(String sql, Object... params) throws SQLException {
        DatabaseConfig config = databaseConfigReader.getDatabaseConfig();

        if (config == null) {
            throw new SQLException("Database configuration not found.");
        }

        try (Connection connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            int index = 1;
            for (Object param : params) {
                if (param != null) {
                    pstmt.setObject(index++, param);
                }
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    private void sendResponse(SlingHttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"success\": " + success + ", \"message\": \"" +
                        message.replace("\"", "\\\"") + "\"}"
        );
        response.setStatus(success ? SlingHttpServletResponse.SC_OK : SlingHttpServletResponse.SC_BAD_REQUEST);
    }
}
