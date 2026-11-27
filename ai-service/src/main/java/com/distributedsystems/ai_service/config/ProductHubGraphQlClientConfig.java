package com.distributedsystems.ai_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductHubGraphQlClientConfig {

    @Bean
    public HttpSyncGraphQlClient productHubHttpGraphQlClient(
            LoadBalancerClient loadBalancerClient
    ) {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://product-hub/graphql")
                .requestInterceptor(
                        new LoadBalancerInterceptor(loadBalancerClient)
                )
                .build();

        return HttpSyncGraphQlClient.create(restClient);
    }
}