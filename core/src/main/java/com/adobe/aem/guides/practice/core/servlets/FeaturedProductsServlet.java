package com.adobe.aem.guides.practice.core.servlets;

import com.adobe.aem.guides.practice.core.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/featured-products") // Define the servlet path
public class FeaturedProductsServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FeaturedProductsServlet.class);

    @Reference
    private ProductService productService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Fetch products from the API
            String jsonResponse = productService.getProducts();
            if (jsonResponse != null) {
                // Return the JSON response directly
                response.getWriter().write(jsonResponse);
            } else {
                response.getWriter().write("{\"error\": \"Failed to fetch products\"}");
            }
        } catch (Exception e) {
            LOG.error("Error in FeaturedProductsServlet", e);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}