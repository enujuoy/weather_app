package com.weather.controller;

import com.weather.entity.Favorite;
import com.weather.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FavoriteHomeController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteHomeController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/favorites")
    public String favoritesPage(Model model, @RequestParam(value = "sessionId", required = false) String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "defaultSessionId"; // 기본 세션 ID 설정
        }
        List<Favorite> favorites = favoriteService.getFavoritesBySessionId(sessionId);
        model.addAttribute("favorites", favorites);
        return "favorites"; // 템플릿 이름
    }
}
