package com.distributedsystems.ai_service.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class GeminiTestController {

    private final ChatModel chatModel;

    public GeminiTestController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping(
        value = "/gemini",
        consumes = MediaType.TEXT_PLAIN_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String testGemini(@RequestBody String message) {
        return chatModel.call(message);
    }
}