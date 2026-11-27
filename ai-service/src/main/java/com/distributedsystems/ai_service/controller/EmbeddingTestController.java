package com.distributedsystems.ai_service.controller;

import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class EmbeddingTestController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingTestController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @PostMapping(
        value = "/embedding",
        consumes = MediaType.TEXT_PLAIN_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> testEmbedding(@RequestBody String text) {
        float[] embedding = embeddingModel.embed(text);

        var sample = IntStream.range(0, Math.min(5, embedding.length))
            .mapToDouble(index -> embedding[index])
            .boxed()
            .toList();

        return Map.of(
            "dimensions", embedding.length,
            "sample", sample
        );
    }
}