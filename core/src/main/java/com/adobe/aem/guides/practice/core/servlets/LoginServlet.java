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
//import java.sql.*;
//
//@Component(
//                    service = Servlet.class,
//                    property = {
//                            "sling.servlet.methods=POST",
//                            "sling.servlet.paths=/bin/loginuser"
//                    }
//            )
//            public class LoginServlet extends SlingAllMethodsServlet {
//
//                private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
//                private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
//
//                @Reference
//                private DatabaseConfigReader databaseConfigReader;
//
//                @Override
//                protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
//                        throws ServletException, IOException {
//
//                    String email = request.getParameter("email");
//                    String password = request.getParameter("password");
//
//                    // Enhanced input validation
//                    if (!isValidInput(email, password)) {
//                        sendResponse(response, false, "Invalid email or password format");
//                        return;
//                    }
//
//                    try {
//                        String authenticationResult = authenticateUser(email, password);
//                        if ("success".equals(authenticationResult)) {
//                            // Set session attribute for logged-in user
//                            request.getSession().setAttribute("loggedInUser", email);
//                            response.sendRedirect(request.getContextPath() + "/content/practice/us/en/homepage.html");
//                        } else {
//                            sendResponse(response, false, authenticationResult);
//                        }
//                    } catch (SQLException e) {
//                        log.error("Database error during login: ", e);
//                        sendResponse(response, false, "Database error occurred");
//                    } catch (Exception e) {
//                        log.error("Error during login: ", e);
//                        sendResponse(response, false, "Internal server error");
//                    }
//                }
//
//                private boolean isValidInput(String email, String password) {
//                    return email != null && !email.isEmpty() &&
//                           password != null && !password.isEmpty() &&
//                           email.matches(EMAIL_REGEX);
//                }
//
//                private String authenticateUser(String email, String password) throws SQLException {
//                    DatabaseConfig config = databaseConfigReader.getDatabaseConfig();
//                    if (config == null) {
//                        throw new IllegalStateException("Database configuration not found");
//                    }
//
//                    try (Connection connection = getConnection(config);
//                         PreparedStatement pstmt = connection.prepareStatement(
//                                 "SELECT password FROM login.users WHERE email = ?")) {
//
//                        pstmt.setString(1, email);
//                        try (ResultSet resultSet = pstmt.executeQuery()) {
//                            if (resultSet.next()) {
//                                String storedHashedPassword = resultSet.getString("password");
//                                String inputHashedPassword = DigestUtils.sha256Hex(password);
//                                return storedHashedPassword.equals(inputHashedPassword) ? "success" : "Incorrect password";
//                            }
//                            return "Email not found";
//                        }
//                    }
//                }
//
//                private Connection getConnection(DatabaseConfig config) throws SQLException {
//                    try {
//                        Class.forName(config.getDriverClass());
//                    } catch (ClassNotFoundException e) {
//                        throw new SQLException("JDBC Driver not found", e);
//                    }
//                    return DriverManager.getConnection(config.getJdbcUrl(),
//                                                     config.getUsername(),
//                                                     config.getPassword());
//                }
//
//                private void sendResponse(SlingHttpServletResponse response, boolean success, String message)
//                        throws IOException {
//                    response.setContentType("application/json");
//                    response.setCharacterEncoding("UTF-8");
//                    String jsonResponse = String.format("{\"success\": %b, \"message\": \"%s\"}",
//                                                     success, message.replace("\"", "\\\""));
//                    response.getWriter().write(jsonResponse);
//                    response.setStatus(success ? SlingHttpServletResponse.SC_OK : SlingHttpServletResponse.SC_BAD_REQUEST);
//                }
//            }




