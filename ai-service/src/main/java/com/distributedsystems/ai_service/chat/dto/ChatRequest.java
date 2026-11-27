package com.distributedsystems.ai_service.chat.dto;

public record ChatRequest(
        String message,
        String conversationId
) {
}