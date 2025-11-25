package com.example.aqi_project.Config;

// backend/src/main/java/com/aqi/infrastructure/config/CacheConfig.java



import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("airQualityCache");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)  // Remove after 10 minutes
                .expireAfterAccess(5, TimeUnit.MINUTES)  // Remove if not accessed for 5 minutes
                .recordStats()  // Enable statistics
                .removalListener((key, value, cause) ->
                        System.out.println("🗑️ Cache entry removed: " + key + " - Cause: " + cause)
                );
    }
}