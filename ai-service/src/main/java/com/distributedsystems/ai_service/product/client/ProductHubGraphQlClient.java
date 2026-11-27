package com.distributedsystems.ai_service.product.client;

import com.distributedsystems.ai_service.product.dto.ProductCatalogResponse;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.stereotype.Component;

@Component
public class ProductHubGraphQlClient {

    private static final String PRODUCTS_QUERY = """
            query Products($page: Int!, $size: Int!) {
                products(page: $page, size: $size) {
                    content {
                        id
                        name
                        description
                        price
                        category
                        stock
                    }
                    totalPages
                    totalElements
                    number
                    size
                }
            }
            """;

    private final HttpSyncGraphQlClient productHubGraphQlClient;

    public ProductHubGraphQlClient(
            HttpSyncGraphQlClient productHubGraphQlClient
    ) {
        this.productHubGraphQlClient = productHubGraphQlClient;
    }

    public ProductCatalogResponse getProducts(int page, int size) {

        return productHubGraphQlClient
                .document(PRODUCTS_QUERY)
                .variable("page", page)
                .variable("size", size)
                .retrieveSync("products")
                .toEntity(ProductCatalogResponse.class);
    }
}