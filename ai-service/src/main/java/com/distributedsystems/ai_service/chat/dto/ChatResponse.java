package com.distributedsystems.ai_service.chat.dto;

public record ChatResponse(
        String message,
        String conversationId
) {
}