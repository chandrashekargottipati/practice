package com.adobe.aem.guides.practice.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/logoutuser"
        }
)
public class LogoutServlet extends SlingAllMethodsServlet {

    // Initialize the logger
    private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Get the current session, don't create a new one
        HttpSession session = request.getSession(false);

        if (session != null) {
            log.info("Invalidating session for user: {}", session.getAttribute("users"));
            session.invalidate();
        } else {
            log.warn("No session found to invalidate.");
        }

        log.info("Redirecting to login page.");
        response.sendRedirect("/content/practice/us/en/login.html");
    }
}
