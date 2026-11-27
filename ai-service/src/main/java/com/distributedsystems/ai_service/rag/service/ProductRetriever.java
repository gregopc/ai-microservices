package com.distributedsystems.ai_service.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRetriever {

    private static final int TOP_K = 5;
    private static final String SOURCE = "product-hub";

    private final VectorStore vectorStore;

    public ProductRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> retrieve(String query) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(TOP_K)
                        .filterExpression("source == '" + SOURCE + "'")
                        .build()
        );
    }
}