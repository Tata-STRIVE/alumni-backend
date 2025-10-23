package com.striveconnect.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global configuration for Cross-Origin Resource Sharing (CORS).
 * This allows the frontend application to communicate with the backend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS configuration to all API endpoints
            .allowedOrigins("http://localhost:5173") // Trust requests from our React development server
            .allowedOrigins( "http://localhost:5173", 
                    "http://strive.localhost:5173", 
                    "http://partnera.localhost:5173") // Trust requests from our React development server
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow standard HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true); // Allow cookies/credentials
    }
}
