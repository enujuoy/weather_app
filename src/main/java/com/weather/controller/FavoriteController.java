package com.weather.controller;

import com.weather.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 즐겨찾기 추가
    @PostMapping
    public ResponseEntity<String> addFavorite(@RequestBody Map<String, String> request, HttpSession session) {
        String sessionId = session.getId();
        String cityName = request.get("cityName");

        if (cityName == null || cityName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("都市名が必要です。");
        }

        try {
            favoriteService.addFavorite(sessionId, cityName);
            return ResponseEntity.ok("お気に入りが追加されました。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("お気に入りの追加中にエラーが発生しました。");
        }
    }

    // 즐겨찾기 조회
    @GetMapping
    public ResponseEntity<?> getFavorites(HttpSession session) {
        String sessionId = session.getId();

        try {
            return ResponseEntity.ok(favoriteService.getFavoritesBySessionId(sessionId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("お気に入りの取得中にエラーが発生しました。");
        }
    }

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<String> deleteFavorite(@RequestBody Map<String, String> request, HttpSession session) {
        String sessionId = session.getId();
        String cityName = request.get("cityName");

        if (cityName == null || cityName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("都市名が必要です。");
        }

        try {
            favoriteService.deleteFavorite(sessionId, cityName);
            return ResponseEntity.ok("お気に入りが削除されました。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("お気に入りの削除中にエラーが発生しました。");
        }
    }
}
