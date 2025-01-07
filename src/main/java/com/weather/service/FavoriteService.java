package com.weather.service;

import com.weather.entity.Favorite;
import com.weather.repository.FavoriteRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    private static final String GEO_API_URL = "http://api.openweathermap.org/geo/1.0/direct";

    public FavoriteService(FavoriteRepository favoriteRepository, RestTemplate restTemplate) {
        this.favoriteRepository = favoriteRepository;
        this.restTemplate = restTemplate;
    }

    // 즐겨찾기 추가
    public void addFavorite(String sessionId, String cityName) throws Exception {
        String url = String.format("%s?q=%s&limit=1&appid=%s", GEO_API_URL, cityName, apiKey);
        Map[] response = restTemplate.getForObject(url, Map[].class);

        if (response == null || response.length == 0) {
            throw new Exception("API에서 도시 정보를 가져오지 못했습니다.");
        }

        Map<String, Object> cityInfo = response[0];
        Double latitude = (Double) cityInfo.get("lat");
        Double longitude = (Double) cityInfo.get("lon");

        Favorite favorite = new Favorite();
        favorite.setSessionId(sessionId);
        favorite.setCityName(cityName);
        favorite.setLatitude(latitude);
        favorite.setLongitude(longitude);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteRepository.save(favorite);
    }
    public Map<String, Double> getCityCoordinates(String cityName) throws Exception {
        String url = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                cityName, apiKey);

        Map[] response = restTemplate.getForObject(url, Map[].class);

        if (response == null || response.length == 0) {
            throw new Exception("도시 좌표를 가져오지 못했습니다.");
        }

        return Map.of(
                "lat", (Double) response[0].get("lat"),
                "lon", (Double) response[0].get("lon")
        );
    }
    // 즐겨찾기 조회
    public List<Favorite> getFavoritesBySessionId(String sessionId) {
        return favoriteRepository.findBySessionId(sessionId);
    }

    // 즐겨찾기 삭제
    @Transactional
    public void deleteFavorite(String sessionId, String cityName) {
        favoriteRepository.deleteBySessionIdAndCityName(sessionId, cityName);
    }
}
