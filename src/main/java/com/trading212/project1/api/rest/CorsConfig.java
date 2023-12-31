package com.trading212.project1.api.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("POST", "GET", "PUT", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization", "Access-Control-Allow-Origin")
            .exposedHeaders("Authorization");
    }
}