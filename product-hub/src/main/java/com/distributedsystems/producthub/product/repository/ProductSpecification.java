package com.distributedsystems.producthub.product.repository;

import com.distributedsystems.producthub.product.entity.Product;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withFilters(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isBlank()) {
                predicates.add(
                    criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        category.trim().toLowerCase()
                    )
                );
            }

            if (minPrice != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                    )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                    )
                );
            }

            if (Boolean.TRUE.equals(inStock)) {
                predicates.add(
                    criteriaBuilder.greaterThan(
                        root.get("stock"),
                        0
                    )
                );
            } else if (Boolean.FALSE.equals(inStock)) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("stock"),
                        0
                    )
                );
            }

            return criteriaBuilder.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}