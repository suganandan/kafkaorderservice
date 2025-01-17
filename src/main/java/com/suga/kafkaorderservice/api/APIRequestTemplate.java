package com.suga.kafkaorderservice.api;


import com.suga.kafkaorderservice.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Component
public class APIRequestTemplate {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${products.base.url}")
    private String productsBaseUrl;

    public Product getProduct(Long productId) throws Exception {
        String url = String.format("%s/api/v1/products/%d", productsBaseUrl, productId);
        try {
            log.info("Sending request to fetch product with ID: {}", productId);
            Product product = restTemplate.getForObject(url, Product.class);
            if (product == null) {
                log.warn("No product found for ID: {}", productId);
            } else {
                log.info("Successfully fetched product with ID: {}", productId);
            }
            return product;
        }  catch (Exception exception) {
            log.error("Unexpected error while fetching product with ID {}: {}", productId, exception.getMessage(), exception);
            throw new Exception("An unexpected error occurred while fetching the product.", exception);
        }
    }



}
