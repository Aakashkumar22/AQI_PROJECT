package com.example.aqi_project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCity {
    private String cityName;
    private LocalDateTime addedAt;
    private Integer lastKnownAqi;
    private String lastKnownQuality;
}

