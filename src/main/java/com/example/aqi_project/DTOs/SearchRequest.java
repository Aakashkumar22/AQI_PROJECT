package com.example.aqi_project.DTOs;

// backend/src/main/java/com/aqi/interfaces/dto/SearchRequest.java


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SearchRequest {
    @NotBlank(message = "City name is required")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    private String city;
}

// backend/src/main/java/com/aqi/interfaces/dto/ApiResponse.java


