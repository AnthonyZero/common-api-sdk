package com.anthonyzero.sdk.client.config;

import com.anthonyzero.sdk.client.OpenApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Configuration
@EnableConfigurationProperties({ OpenApiProperties.class })
public class OpenApiAutoConfiguration {

    @Bean
    public OpenApiClient openApiClient(OpenApiProperties properties) {
        return new OpenApiClient(properties);
    }
}
