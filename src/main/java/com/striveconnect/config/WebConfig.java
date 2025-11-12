package com.striveconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // ✅ Apply to all endpoints
                .allowedOriginPatterns(
                    "http://localhost:5173",
                    "http://strive.localhost:5173",
                    "http://partnera.localhost:5173"
                ) // ✅ use patterns instead of allowedOrigins()
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
