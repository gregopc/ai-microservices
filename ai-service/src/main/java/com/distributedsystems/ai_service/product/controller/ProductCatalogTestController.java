package com.distributedsystems.ai_service.product.controller;

import com.distributedsystems.ai_service.product.service.ProductCatalogVectorStoreLoader;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/catalog")
public class ProductCatalogTestController {

    private final ProductCatalogVectorStoreLoader productCatalogVectorStoreLoader;

    public ProductCatalogTestController(
            ProductCatalogVectorStoreLoader productCatalogVectorStoreLoader
    ) {
        this.productCatalogVectorStoreLoader = productCatalogVectorStoreLoader;
    }

    @PostMapping("/load")
    public CatalogLoadResponse loadCatalog() {

        int loadedProducts =
                productCatalogVectorStoreLoader.loadCatalog();

        return new CatalogLoadResponse(loadedProducts);
    }

    public record CatalogLoadResponse(int loadedProducts) {
    }
}