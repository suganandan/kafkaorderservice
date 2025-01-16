package com.suga.kafkaorderservice.api;


import com.suga.kafkaorderservice.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class APIRequestTemplate {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${products.base.url}")
    private String productsBaseUrl;

    public Product getProduct(Long productId) throws Exception {
        return restTemplate.getForObject(productsBaseUrl + "/api/products/" + productId, Product.class);
    }


}
