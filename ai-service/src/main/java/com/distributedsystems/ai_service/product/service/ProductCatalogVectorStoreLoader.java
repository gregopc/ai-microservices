package com.distributedsystems.ai_service.product.service;

import com.distributedsystems.ai_service.product.client.ProductHubGraphQlClient;
import com.distributedsystems.ai_service.product.dto.ProductCatalogItem;
import com.distributedsystems.ai_service.product.dto.ProductCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductCatalogVectorStoreLoader {

    private static final Logger log =
            LoggerFactory.getLogger(ProductCatalogVectorStoreLoader.class);

    private static final int PAGE_SIZE = 50;
    private static final int BATCH_SIZE = 50;
    private static final String SOURCE = "product-hub";

    private final ProductHubGraphQlClient productHubGraphQlClient;
    private final VectorStore vectorStore;

    public ProductCatalogVectorStoreLoader(
            ProductHubGraphQlClient productHubGraphQlClient,
            VectorStore vectorStore
    ) {
        this.productHubGraphQlClient = productHubGraphQlClient;
        this.vectorStore = vectorStore;
    }

    public int loadCatalog() {

        List<ProductCatalogItem> products = fetchAllProducts();

        log.info("Retrieved {} products from Product Hub", products.size());

        if (products.isEmpty()) {
            log.warn("Product Hub returned an empty catalog. Existing documents were preserved.");
            return 0;
        }

        List<Document> documents = products.stream()
                .map(this::toDocument)
                .toList();

        // Remove the previous catalog before loading the new one.
        vectorStore.delete("source == '" + SOURCE + "'");

        // Insert documents in batches.
        for (int start = 0; start < documents.size(); start += BATCH_SIZE) {

            int end = Math.min(start + BATCH_SIZE, documents.size());

            vectorStore.add(documents.subList(start, end));
        }

        log.info("Loaded {} products into the Vector Store", documents.size());

        return documents.size();
    }

    private List<ProductCatalogItem> fetchAllProducts() {

        List<ProductCatalogItem> products = new ArrayList<>();

        int page = 0;
        int totalPages;

        do {
            ProductCatalogResponse response =
                    productHubGraphQlClient.getProducts(page, PAGE_SIZE);

            products.addAll(response.content());

            totalPages = response.totalPages();

            page++;

        } while (page < totalPages);

        return products;
    }

    private Document toDocument(ProductCatalogItem product) {

        StringBuilder content = new StringBuilder();

        content.append("Product: ").append(product.name());

        if (product.category() != null) {
            content.append("\nCategory: ").append(product.category());
        }

        if (product.description() != null) {
            content.append("\nDescription: ").append(product.description());
        }

        if (product.price() != null) {
            content.append("\nPrice: ").append(product.price());
        }

        if (product.stock() != null) {
            content.append("\nStock: ").append(product.stock());
        }

        Document.Builder builder = Document.builder()
                .id(product.id().toString())
                .text(content.toString())
                .metadata("source", SOURCE)
                .metadata("productId", product.id().toString());

        if (product.category() != null) {
            builder.metadata("category", product.category());
        }

        if (product.price() != null) {
            builder.metadata("price", product.price().toPlainString());
        }

        if (product.stock() != null) {
            builder.metadata("stock", product.stock());
            builder.metadata("inStock", product.stock() > 0);
        }

        return builder.build();
    }
}