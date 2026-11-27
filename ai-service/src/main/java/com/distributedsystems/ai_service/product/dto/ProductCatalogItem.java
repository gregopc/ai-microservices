package com.distributedsystems.ai_service.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCatalogItem(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stock
) {
}