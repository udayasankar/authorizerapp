package com.sample.anamoly.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffineConfiguration {

    @Bean
    public Cache<String, String> getCaffineCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .build();
    }
}
