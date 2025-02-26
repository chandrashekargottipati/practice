package com.adobe.aem.guides.practice.core.models;

import com.adobe.aem.guides.practice.core.services.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class)
public class CategoryListModel {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryListModel.class);
    private static final String DEFAULT_IMAGE_PATH = "/content/dam/practice/multishop/default-category.jpg";

    @OSGiService
    private CategoryService categoryService;

    private List<Category> categories = new ArrayList<>();

    @PostConstruct
    protected void init() {
        try {
            String jsonResponse = categoryService.getCategories();
            if (jsonResponse != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> categoryNames = objectMapper.readValue(jsonResponse, new TypeReference<List<String>>() {});

                for (String category : categoryNames) {
                    // Replace apostrophe (') and spaces with proper encoding or remove if needed
                    String sanitizedCategory = category.replace("'", "").replace(" ", "-").toLowerCase();
                    String categoryImagePath = "/content/dam/practice/multishop/" + sanitizedCategory + ".jpg";
                    categories.add(new Category(category, categoryImagePath));
                }
            }
        } catch (Exception e) {
            LOG.error("Error parsing API response: {}", e.getMessage());
        }
    }


    public List<Category> getCategories() {
        return categories;
    }

    public static class Category {
        private String title;
        private String image;

        public Category(String title, String image) {
            this.title = title;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }
    }
}
