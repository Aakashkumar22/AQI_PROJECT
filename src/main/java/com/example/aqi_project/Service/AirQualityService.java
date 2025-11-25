package com.example.aqi_project.Service;

import com.example.aqi_project.Models.AirQualityData;


public interface AirQualityService {
    AirQualityData getAirQualityByCity(String city);

}