package com.weather.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class WeatherController {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/weather")
    public ResponseEntity<String> getWeather(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lon", required = false) Double lon,
            @RequestParam(value = "location", required = false) String location) {

        try {
            String url;

            if (lat != null && lon != null) {
                // 위도와 경도를 이용해 날씨 데이터 요청
                url = String.format(
                        "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric&lang=ja",
                        lat, lon, apiKey);
            } else if (location != null && !location.isEmpty()) {
                // 위치 이름을 이용해 날씨 데이터 요청
                String geocodeUrl = String.format(
                        "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                        location, apiKey);

                ResponseEntity<String> geocodeResponse = restTemplate.getForEntity(geocodeUrl, String.class);
                if (!geocodeResponse.getStatusCode().is2xxSuccessful() || geocodeResponse.getBody() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("位置情報が見つかりません。");
                }

                JsonNode geocodeJsonArray = objectMapper.readTree(geocodeResponse.getBody());
                if (geocodeJsonArray.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("位置情報が見つかりません。");
                }

                JsonNode locationJson = geocodeJsonArray.get(0);
                lat = locationJson.get("lat").asDouble();
                lon = locationJson.get("lon").asDouble();

                url = String.format(
                        "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric&lang=ja",
                        lat, lon, apiKey);
            } else {
                // 파라미터가 부족할 경우
                return ResponseEntity.badRequest().body("位置情報が必要です。");
            }

            // OpenWeatherMap API 호출
            String forecastResponse = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(forecastResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("天気情報の取得に失敗しました。");
        }
       
    }
    @GetMapping("/api/key")
    public ResponseEntity<String> getApiKey() {
        return ResponseEntity.ok(apiKey);
    }

    
}
