package com.example.aqi_project.Models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityData {
    private String city;
    private Integer aqi;
    private String quality;
    private String dominantPollutant;
    private Map<String, Double> pollutants;
    private LocalDateTime timestamp;
    private WeatherData weather;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherData {
        private Double temperature;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private String windDirection;
    }
}
