package com.distributedsystems.producthub.product.controller;

import com.distributedsystems.producthub.product.dto.ProductResponse;
import com.distributedsystems.producthub.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ProductGraphQlController {

    private final ProductService productService;

    @QueryMapping
    public Page<ProductResponse> products(
            @Argument String category,
            @Argument BigDecimal minPrice,
            @Argument BigDecimal maxPrice,
            @Argument Boolean inStock,
            @Argument int page,
            @Argument int size,
            @Argument String sortBy,
            @Argument String sortDirection
    ) {
        return productService.getProducts(
                category,
                minPrice,
                maxPrice,
                inStock,
                page,
                size,
                sortBy,
                sortDirection
        );
    }
}