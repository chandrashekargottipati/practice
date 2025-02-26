package com.adobe.aem.guides.practice.core.models;

import com.adobe.aem.guides.practice.core.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeaturedProductsModel {

    private static final Logger LOG = LoggerFactory.getLogger(FeaturedProductsModel.class);

    @SlingObject
    private Resource resource;

    @Reference
    private ProductService productService;

    private List<Product> products = new ArrayList<>();

    @PostConstruct
    protected void init() {
        try {
            String jsonResponse = productService.getProducts();
            if (jsonResponse != null) {
                ObjectMapper mapper = new ObjectMapper();
                Product[] productArray = mapper.readValue(jsonResponse, Product[].class);
                LOG.info("Number of products fetched: {}", productArray.length); // Log the number of products
                for (Product p : productArray) {
                    products.add(p);
                    LOG.debug("Product added: {}", p.getTitle()); // Log each product title
                }
            } else {
                LOG.error("Failed to fetch products: API returned null");
            }
        } catch (IOException e) {
            LOG.error("Error parsing product JSON", e);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = rating % 1 >= 0.5;
        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

        StringBuilder starsHtml = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            starsHtml.append("<small class=\"fa fa-star text-primary mr-1\"></small>");
        }
        if (halfStar) {
            starsHtml.append("<small class=\"fa fa-star-half-alt text-primary mr-1\"></small>");
        }
        for (int i = 0; i < emptyStars; i++) {
            starsHtml.append("<small class=\"far fa-star text-primary mr-1\"></small>");
        }
        return starsHtml.toString();
    }

    // Product class to map API response


    public static class Product {
        private String title;
        private double price;
        private String image;
        private Rating rating;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public Rating getRating() { return rating; }
        public void setRating(Rating rating) { this.rating = rating; }
        public String getOriginalPrice() { return String.format("%.2f", price * 1.2); }
    }

    public static class Rating {
        private double rate;
        private int count;

        // Getters and Setters
        public double getRate() { return rate; }
        public void setRate(double rate) { this.rate = rate; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
