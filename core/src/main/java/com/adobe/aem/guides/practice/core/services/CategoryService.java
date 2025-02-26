// /apps/myproject/core/src/main/java/com/myproject/core/services/CategoryService.java
package com.adobe.aem.guides.practice.core.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service = CategoryService.class, immediate = true)
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    private static final String API_BASE_URL = "https://fakestoreapi.com";

    public String getCategories() {
        String apiUrl = API_BASE_URL + "/products/categories";
        return callApi(apiUrl);
    }

    public String getProductsByCategory(String category) {
        String apiUrl = API_BASE_URL + "/products/category/" + category;
        return callApi(apiUrl);
    }

    private String callApi(String apiUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOG.error("Error calling API: {}", e.getMessage());
            return null;
        }
    }
}