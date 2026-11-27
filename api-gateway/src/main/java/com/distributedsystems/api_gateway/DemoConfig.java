package com.distributedsystems.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class DemoConfig {

    @Value("${app.demo-message}")
    private String message;

    public String getMessage() {
        return message;
    }
}