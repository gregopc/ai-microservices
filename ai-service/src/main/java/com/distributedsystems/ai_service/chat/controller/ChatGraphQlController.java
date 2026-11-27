package com.distributedsystems.ai_service.chat.controller;

import com.distributedsystems.ai_service.chat.dto.ChatRequest;
import com.distributedsystems.ai_service.chat.dto.ChatResponse;
import com.distributedsystems.ai_service.chat.service.ChatService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatGraphQlController {

    private final ChatService chatService;

    public ChatGraphQlController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MutationMapping
    public ChatResponse chat(@Argument ChatRequest input) {
        return chatService.chat(input);
    }
}