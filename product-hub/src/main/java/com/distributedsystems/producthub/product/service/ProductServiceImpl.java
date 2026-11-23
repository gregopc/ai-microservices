package com.distributedsystems.producthub.product.service;

import com.distributedsystems.producthub.product.ProductSortField;
import com.distributedsystems.producthub.product.dto.ProductRequest;
import com.distributedsystems.producthub.product.dto.ProductResponse;
import com.distributedsystems.producthub.product.dto.ProductUpdateRequest;
import com.distributedsystems.producthub.product.entity.Product;
import com.distributedsystems.producthub.product.mapper.ProductMapper;
import com.distributedsystems.producthub.product.repository.ProductRepository;
import com.distributedsystems.producthub.product.repository.ProductSpecification;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final List<String> STOPWORDS = List.of(
            "para", "com", "de", "o", "a", "e", "do", "da"
    );

    private static final int NAME_WEIGHT = 3;
    private static final int CATEGORY_WEIGHT = 2;
    private static final int DESCRIPTION_WEIGHT = 1;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        validateProduct(
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        Product product = productMapper.toEntity(request);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = findProductOrThrow(id);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        validatePagination(page, size);
        validatePriceRange(minPrice, maxPrice);

        ProductSortField sortField =
                ProductSortField.fromString(sortBy);

        Sort.Direction direction =
                Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField.getFieldName())
        );

        Specification<Product> specification =
                ProductSpecification.withFilters(
                        category,
                        minPrice,
                        maxPrice,
                        inStock
                );

        return productRepository.findAll(specification, pageable)
                .map(productMapper::toResponse);
    }

    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            String query,
            int page,
            int size
    ) {
        validatePagination(page, size);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query must not be blank."
            );
        }

        List<String> searchTerms = List.of(normalize(query).split("\\s+"))
                .stream()
                .filter(term -> !term.isBlank())
                .filter(term -> !STOPWORDS.contains(term))
                .toList();

        if (searchTerms.isEmpty()) {
            throw new IllegalArgumentException(
                    "Search query must contain meaningful terms."
            );
        }

        List<Product> products = productRepository.findAll();

        List<ProductSearchResult> results = new ArrayList<>();

        for (Product product : products) {
            int score = calculateScore(product, searchTerms);

            if (score > 0) {
                results.add(new ProductSearchResult(product, score));
            }
        }

        results.sort(
                Comparator.comparingInt(ProductSearchResult::score)
                        .reversed()
                        .thenComparing(
                                result -> result.product().getName(),
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        long startIndex = (long) page * size;

        int start = (int) Math.min(startIndex, results.size());
        int end = Math.min(start + size, results.size());

        List<ProductResponse> content = results.subList(start, end)
                .stream()
                .map(result -> productMapper.toResponse(result.product()))
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(content, pageable, results.size());
    }


    @Override
    public ProductResponse updateProduct(
            UUID id,
            ProductUpdateRequest request
    ) {
        Product product = findProductOrThrow(id);

        if (request.getName() != null) {
            validateName(request.getName());
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            validatePrice(request.getPrice());
            product.setPrice(request.getPrice());
        }

        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }

        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        if (request.getStock() != null) {
            validateStock(request.getStock());
            product.setStock(request.getStock());
        }

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = findProductOrThrow(id);

        productRepository.delete(product);
    }

    private Product findProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id: " + id
                ));
    }

    private int calculateScore(
            Product product,
            List<String> searchTerms
    ) {
        String name = normalize(product.getName());
        String category = normalize(product.getCategory());
        String description = normalize(product.getDescription());

        int score = 0;

        for (String term : searchTerms) {
            if (name.contains(term)) {
                score += NAME_WEIGHT;
            }

            if (category.contains(term)) {
                score += CATEGORY_WEIGHT;
            }

            if (description.contains(term)) {
                score += DESCRIPTION_WEIGHT;
            }
        }

        return score;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(
                value.toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        return normalized
                .replaceAll("\\p{M}", "")
                .trim();
    }

    private void validateProduct(
            String name,
            BigDecimal price,
            Integer stock
    ) {
        validateName(name);
        validatePrice(price);
        validateStock(stock);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name must not be blank."
            );
        }

        if (name.length() < 3 || name.length() > 150) {
            throw new IllegalArgumentException(
                    "Product name must contain between 3 and 150 characters."
            );
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Product price must be greater than zero."
            );
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException(
                    "Product stock must be zero or greater."
            );
        }
    }

    private void validatePriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price."
            );
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page index cannot be negative."
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100."
            );
        }
    }

    private record ProductSearchResult(
            Product product,
            int score
    ) {
    }
}