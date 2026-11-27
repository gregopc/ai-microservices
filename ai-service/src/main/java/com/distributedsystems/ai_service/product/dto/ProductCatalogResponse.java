package com.distributedsystems.ai_service.product.dto;

import java.util.List;

public record ProductCatalogResponse(
        List<ProductCatalogItem> content,
        int totalPages,
        long totalElements,
        int number,
        int size
) {
}