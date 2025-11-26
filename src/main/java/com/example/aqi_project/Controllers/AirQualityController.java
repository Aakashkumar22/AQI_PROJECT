package com.example.aqi_project.Controllers;

// backend/src/main/java/com/aqi/interfaces/controller/AirQualityController.java



import com.example.aqi_project.DTOs.ApiResponse;
import com.example.aqi_project.DTOs.SearchRequest;
import com.example.aqi_project.Models.AirQualityData;
import com.example.aqi_project.Service.CachedAirQualityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/air-quality")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AirQualityController {

    private final CachedAirQualityService cachedAirQualityService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<AirQualityData>> searchAirQuality(
            @Valid @RequestBody SearchRequest request) {
        try {
            log.info("Searching air quality for city: {}", request.getCity());

            AirQualityData airQualityData = cachedAirQualityService.getAirQualityByCity(request.getCity());

            return ResponseEntity.ok(ApiResponse.success(airQualityData));

        } catch (Exception e) {
            log.error("Error searching air quality for city: {}", request.getCity(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch air quality data: " + e.getMessage()));
        }
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<ApiResponse<CachedAirQualityService.CacheStats>> getCacheStats() {
        return ResponseEntity.ok(ApiResponse.success(cachedAirQualityService.getCacheStats()));
    }

    @DeleteMapping("/cache/{city}")
    public ResponseEntity<ApiResponse<Void>> evictCityCache(@PathVariable String city) {
        cachedAirQualityService.evictCityCache(city);
        return ResponseEntity.ok(ApiResponse.success(null, "Cache evicted for city: " + city));
    }
    @GetMapping("/cache/test/max-size")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testMaxSize() {
        Map<String, Object> result = new HashMap<>();

        // Add multiple cities to test maximum size
        String[] testCities = {"london", "paris", "berlin", "tokyo", "delhi"};
        List<String> responses = new ArrayList<>();

        for (String city : testCities) {
            try {
                AirQualityData data = cachedAirQualityService.getAirQualityByCity(city);
                responses.add("Fetched: " + city);
            } catch (Exception e) {
                responses.add("Error: " + city + " - " + e.getMessage());
            }
        }

        result.put("testCities", responses);
        result.put("currentStats", cachedAirQualityService.getCacheStats());
        result.put("message", "Added " + testCities.length + " cities to test maximum cache size (max: 3)");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/cache/test/expiration")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testExpiration() {
        Map<String, Object> result = new HashMap<>();

        // First, add some data
        cachedAirQualityService.getAirQualityByCity("london");
        cachedAirQualityService.getAirQualityByCity("paris");

        result.put("beforeExpiration", cachedAirQualityService.getDetailedCacheInfo());
        result.put("message", "Data cached. Wait 1+ minute and check /cache/stats again");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/cache/test/force-expire")
    public ResponseEntity<ApiResponse<Map<String, Object>>> forceExpireCache(
            @RequestBody Map<String, String> request) {
        String city = request.get("city");
        Map<String, Object> result = new HashMap<>();

        cachedAirQualityService.forceExpireCityCache(city);

        result.put("message", "Forced expiration for city: " + city);
        result.put("currentStats", cachedAirQualityService.getCacheStats());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/cache/test/check-expired")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkExpiredEntries() {
        Map<String, Object> result = new HashMap<>();

        cachedAirQualityService.checkAndExpireCache();

        result.put("afterExpirationCheck", cachedAirQualityService.getDetailedCacheInfo());
        result.put("message", "Manual expiration check completed");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/cache/detailed-info")
    public ResponseEntity<ApiResponse<CachedAirQualityService.DetailedCacheInfo>> getDetailedCacheInfo() {
        return ResponseEntity.ok(ApiResponse.success(cachedAirQualityService.getDetailedCacheInfo()));
    }
}

