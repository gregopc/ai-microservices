package com.distributedsystems.ai_service.product.controller;

import com.distributedsystems.ai_service.product.client.ProductHubGraphQlClient;
import com.distributedsystems.ai_service.product.dto.ProductCatalogResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductHubTestController {

    private final ProductHubGraphQlClient productHubGraphQlClient;

    public ProductHubTestController(
            ProductHubGraphQlClient productHubGraphQlClient
    ) {
        this.productHubGraphQlClient = productHubGraphQlClient;
    }

    @GetMapping("/test/product-hub")
    public ProductCatalogResponse getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productHubGraphQlClient.getProducts(page, size);
    }
}