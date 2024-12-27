package com.weather.repository;

import com.weather.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findBySessionId(String sessionId);

    void deleteBySessionIdAndCityName(String sessionId, String cityName);
}
