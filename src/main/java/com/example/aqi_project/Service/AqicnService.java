package com.example.aqi_project.Service;

import com.example.aqi_project.Models.AirQualityData;
import com.example.aqi_project.DTOs.AqicnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AqicnService implements AirQualityService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public AqicnService(
            @Value("${aqicn.api.key:demo}") String apiKey,
            @Value("${aqicn.api.url:https://api.waqi.info}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public AirQualityData getAirQualityByCity(String city) {
        try {
            // Use HTTPS and proper city formatting
            String url = baseUrl + "/feed/" + city.toLowerCase() + "/?token=" + apiKey;

            log.info("Making API request for city: {}", city);

            ResponseEntity<AqicnResponse> response = restTemplate.getForEntity(url, AqicnResponse.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Empty response from AQICN API");
            }

            AqicnResponse apiResponse = response.getBody();

            if (!"ok".equals(apiResponse.getStatus())) {
                throw new RuntimeException("API error: " + apiResponse.getStatus());
            }

            // Validate that we got data for the requested city
            String returnedCity = apiResponse.getData().getCity().getName();
            if (!isCityMatch(city, returnedCity)) {
                log.warn("City mismatch: requested '{}', got '{}'", city, returnedCity);
                throw new RuntimeException("API returned data for different city: " + returnedCity);
            }

            return convertToDomainModel(apiResponse);

        } catch (Exception e) {
            log.error("Error fetching air quality data for city: {}", city, e);
            throw new RuntimeException("Failed to fetch air quality data for " + city + ": " + e.getMessage());
        }
    }

    private boolean isCityMatch(String requestedCity, String returnedCity) {
        // Basic city name matching logic
        String requested = requestedCity.toLowerCase().trim();
        String returned = returnedCity.toLowerCase().trim();

        return returned.contains(requested) || requested.contains(returned);
    }




    private AirQualityData convertToDomainModel(AqicnResponse response) {
        AqicnResponse.Data data = response.getData();

        Map<String, Double> pollutants = new HashMap<>();
        if (data.getIaqi() != null) {
            data.getIaqi().forEach((key, value) -> {
                if (value.getV() != null) {
                    pollutants.put(key, value.getV());
                }
            });
        }

        String quality = determineQualityLevel(data.getAqi());
        String dominantPollutant = determineDominantPollutant(pollutants);
        AirQualityData.WeatherData weatherData = extractWeatherData(pollutants);

        log.info("Successfully converted data for {}: AQI={}, Quality={}",
                data.getCity().getName(), data.getAqi(), quality);

        return new AirQualityData(
                data.getCity().getName(),
                data.getAqi(),
                quality,
                dominantPollutant,
                pollutants,
                java.time.LocalDateTime.now(),
                weatherData


        );
    }


    private AirQualityData.WeatherData extractWeatherData(Map<String, Double> pollutants) {
        if (pollutants == null || pollutants.isEmpty()) {
            return null;
        }

        AirQualityData.WeatherData weatherData = new AirQualityData.WeatherData();

        try {
            // Temperature (°C) - 't' parameter
            if (pollutants.containsKey("t")) {
                weatherData.setTemperature(pollutants.get("t"));
            }

            // Humidity (%) - 'h' parameter
            if (pollutants.containsKey("h")) {
                weatherData.setHumidity(pollutants.get("h"));
            }

            // Pressure (hPa) - 'p' parameter
            if (pollutants.containsKey("p")) {
                weatherData.setPressure(pollutants.get("p"));
            }

            // Wind Speed (m/s or km/h) - 'w' parameter
            if (pollutants.containsKey("w")) {
                weatherData.setWindSpeed(pollutants.get("w"));
            }

            // Wind Direction - check for common wind direction parameters
            String windDirection = determineWindDirection(pollutants);
            weatherData.setWindDirection(windDirection);

        } catch (Exception e) {
            log.warn("Error extracting weather data from pollutants: {}", e.getMessage());
            // Return the partially populated weather data instead of null
        }

        // Return null only if no weather data was extracted at all
        if (weatherData.getTemperature() == null &&
                weatherData.getHumidity() == null &&
                weatherData.getPressure() == null &&
                weatherData.getWindSpeed() == null) {
            return null;
        }

        return weatherData;
    }

    private String determineWindDirection(Map<String, Double> pollutants) {
        // Check for common wind direction parameters in AQICN API
        if (pollutants.containsKey("wd")) {
            Double windDegrees = pollutants.get("wd");
            if (windDegrees != null) {
                return convertDegreesToDirection(windDegrees);
            }
        }

        // Check for other possible wind direction parameters


        // If no wind direction data is available
        return "NA";
    }

    private String convertDegreesToDirection(Double degrees) {
        if (degrees == null) return "N/A";

        // Convert degrees to compass direction
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) ((degrees + 11.25) / 22.5) % 16;
        return directions[index];
    }




    private String determineQualityLevel(Integer aqi) {
        if (aqi == null) return "Unknown";
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

    private String determineDominantPollutant(Map<String, Double> pollutants) {
        return pollutants.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

}