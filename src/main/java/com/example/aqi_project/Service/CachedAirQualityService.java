package com.example.aqi_project.Service;


import com.example.aqi_project.Models.AirQualityData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CachedAirQualityService {

    private final AirQualityService airQualityService;
    private final CacheManager cacheManager;
    private final ConcurrentHashMap<String, AirQualityData> cache;
    private final ConcurrentHashMap<String, LocalDateTime> cacheAccessLog;
    private final AtomicInteger cacheHits;
    private final AtomicInteger cacheMisses;
    private final AtomicInteger cacheEvictions;

    public CachedAirQualityService(AirQualityService airQualityService, CacheManager cacheManager) {
        this.airQualityService = airQualityService;
        this.cacheManager = cacheManager;
        this.cache = new ConcurrentHashMap<>();
        this.cacheAccessLog = new ConcurrentHashMap<>();
        this.cacheHits = new AtomicInteger(0);
        this.cacheMisses = new AtomicInteger(0);
        this.cacheEvictions = new AtomicInteger(0);
    }

    public AirQualityData getAirQualityByCity(String city) {
        String cacheKey = city.toLowerCase();

        // Check cache first
        if (cache.containsKey(cacheKey)) {
            cacheHits.incrementAndGet();
            log.info("✅ CACHE HIT for city: {} - Total hits: {}", city, cacheHits.get());
            cacheAccessLog.put(cacheKey, LocalDateTime.now());
            return cache.get(cacheKey);
        } else {
            cacheMisses.incrementAndGet();
            log.info("🚀 CACHE MISS for city: {} - Total misses: {}", city, cacheMisses.get());

            // Check if we're at maximum size and need to evict
            if (cache.size() >= 3) { // Match the maximumSize in config
                evictOldestEntry();
            }

            // Fetch from service
            AirQualityData data = airQualityService.getAirQualityByCity(city);

            // Store in cache
            cache.put(cacheKey, data);
            cacheAccessLog.put(cacheKey, LocalDateTime.now());

            return data;
        }
    }

    private void evictOldestEntry() {
        cacheAccessLog.entrySet().stream()
                .min(java.util.Map.Entry.comparingByValue())
                .ifPresent(oldest -> {
                    String keyToRemove = oldest.getKey();
                    cache.remove(keyToRemove);
                    cacheAccessLog.remove(keyToRemove);
                    cacheEvictions.incrementAndGet();
                    log.info("📦 MAX SIZE REACHED - Evicted oldest entry: {}", keyToRemove);
                });
    }

    // Manual cache expiration check
    public void checkAndExpireCache() {
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = 0;

        for (var entry : cacheAccessLog.entrySet()) {
            String key = entry.getKey();
            LocalDateTime accessTime = entry.getValue();

            // Check if entry is older than 1 minute (expireAfterWrite)
            if (ChronoUnit.MINUTES.between(accessTime, now) >= 1) {
                cache.remove(key);
                cacheAccessLog.remove(key);
                expiredCount++;
                log.info("⏰ TIME EXPIRED - Removed: {}", key);
            }
        }

        if (expiredCount > 0) {
            log.info("🕒 Expired {} cache entries due to time limit", expiredCount);
        }
    }

    public void evictAllCache() {
        log.info("🗑️ Evicting all cache entries");
        cache.clear();
        cacheAccessLog.clear();
    }

    public void evictCityCache(String city) {
        log.info("🗑️ Evicting cache for city: {}", city);
        String cacheKey = city.toLowerCase();
        cache.remove(cacheKey);
        cacheAccessLog.remove(cacheKey);
    }

    // Force cache expiration for testing
    public void forceExpireCityCache(String city) {
        String cacheKey = city.toLowerCase();
        if (cacheAccessLog.containsKey(cacheKey)) {
            // Set access time to 2 minutes ago to force expiration
            cacheAccessLog.put(cacheKey, LocalDateTime.now().minusMinutes(2));
            log.info("🔧 FORCED EXPIRATION - City: {}", city);
        }
    }

    public CacheStats getCacheStats() {
        int hits = cacheHits.get();
        int misses = cacheMisses.get();
        int evictions = cacheEvictions.get();
        int total = hits + misses;
        double hitRatio = total > 0 ? (double) hits / total : 0.0;

        log.info("📊 Cache Stats - Hits: {}, Misses: {}, Evictions: {}, Hit Ratio: {:.2f}, Size: {}",
                hits, misses, evictions, hitRatio, cache.size());

        return new CacheStats(hits, misses, evictions, hitRatio, cache.size());
    }

    public DetailedCacheInfo getDetailedCacheInfo() {
        return new DetailedCacheInfo(
                cacheHits.get(),
                cacheMisses.get(),
                cacheEvictions.get(),
                cache.size(),
                new java.util.HashMap<>(cacheAccessLog)
        );
    }

    public record CacheStats(int hits, int misses, int evictions, double hitRatio, int size) {}

    public record DetailedCacheInfo(int hits, int misses, int evictions, int size,
                                    java.util.Map<String, LocalDateTime> cacheEntries) {}
}