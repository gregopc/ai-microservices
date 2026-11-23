package com.distributedsystems.producthub.product.service;

import com.distributedsystems.producthub.product.dto.ProductRequest;
import com.distributedsystems.producthub.product.dto.ProductResponse;
import com.distributedsystems.producthub.product.dto.ProductUpdateRequest;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getProducts(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    Page<ProductResponse> searchProducts(
            String query,
            int page,
            int size
    );

    ProductResponse updateProduct(
            UUID id,
            ProductUpdateRequest request
    );

    void deleteProduct(UUID id);
}