package com.distributedsystems.producthub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Hub API",
                version = "1.0",
                description = "API for product catalog management"
        )
)
public class OpenApiConfig {
}