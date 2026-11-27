package com.distributedsystems.ai_service.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/test/vector-store")
public class VectorStoreTestController {

    private final VectorStore vectorStore;

    public VectorStoreTestController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public Map<String, String> insert(@RequestBody String content) {
        String id = UUID.randomUUID().toString();

        Document document = new Document(
            id,
            content,
            Map.of("source", "vector-store-test")
        );

        vectorStore.add(List.of(document));

        return Map.of(
            "message", "Document inserted successfully",
            "id", id
        );
    }

    @GetMapping("/search")
    public List<Map<String, String>> search(
        @RequestParam String query
    ) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .build()
            )
            .stream()
            .map(document -> Map.of(
                "id", document.getId(),
                "content", document.getText()
            ))
            .toList();
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {

        vectorStore.delete(List.of(id));

        return Map.of(
            "message", "Document deleted successfully",
            "id", id
        );
    }
}