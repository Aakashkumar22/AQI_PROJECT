package com.example.aqi_project.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AqicnResponse {
    private String status;
    private Data data;

    @lombok.Data
    public static class Data {
        private Integer aqi;
        private Integer idx;
        private City city;
        private Map<String, Pollutant> iaqi;
        private Time time;

        @lombok.Data
        public static class City {
            private String name;
            private String url;
        }

        @lombok.Data
        public static class Pollutant {
            private Double v;
        }

        @lombok.Data
        public static class Time {
            private String s;
            private String tz;
        }
    }
}
