package com.adobe.aem.guides.practice.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=FakeStore API Product Servlet",
                "sling.servlet.paths=/bin/featured-products",
                "sling.servlet.methods=GET"
        })
public class ProductServlet extends SlingAllMethodsServlet {

    private static final String API_URL = "https://fakestoreapi.com/products";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get product ID from request
        String productId = request.getParameter("id");
        String apiUrl = (productId != null) ? API_URL + "/" + productId : API_URL;

        // Debugging - Print API URL to check if the correct endpoint is called
        System.out.println("Fetching data from: " + apiUrl);

        HttpURLConnection conn = null;
        InputStream inputStream = null;

        try {
            // Establish connection
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                response.getWriter().write("{\"error\": \"Failed to fetch products, HTTP Code: " + responseCode + "\"}");
                return;
            }

            // Read API response
            inputStream = conn.getInputStream();
            JsonParser parser = new JsonParser();
            InputStreamReader reader = new InputStreamReader(inputStream);
            Object json = parser.parse(reader);

            // If an ID was passed, return a single product
            if (productId != null) {
                if (json instanceof JsonObject) {
                    response.getWriter().write(((JsonObject) json).toString());
                } else {
                    response.getWriter().write("{\"error\": \"Invalid response format for product ID: " + productId + "\"}");
                }
            } else {
                // Return full product list
                response.getWriter().write(((JsonArray) json).toString());
            }

        } catch (Exception e) {
            response.getWriter().write("{\"error\": \"An error occurred while fetching products: " + e.getMessage() + "\"}");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
