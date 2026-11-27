package com.distributedsystems.api_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoConfigController {

    private final DemoConfig demoConfig;

    public DemoConfigController(DemoConfig demoConfig) {
        this.demoConfig = demoConfig;
    }

    @GetMapping("/demo/config")
    public String getMessage() {
        return demoConfig.getMessage();
    }
}