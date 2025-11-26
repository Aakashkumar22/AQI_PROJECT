package com.example.aqi_project.Service;

import com.example.aqi_project.Models.FavoriteCity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FavoritesService {
    private static final Map<String, FavoriteCity> favorites = new ConcurrentHashMap<>();

    public static FavoriteCity addFavorite(String city) {
        FavoriteCity favorite = new FavoriteCity(city, LocalDateTime.now(), null, null);
        favorites.put(city.toLowerCase(), favorite);
        log.info("Added favorite city: {}", city);
        return favorite;
    }

    public static void removeFavorite(String city) {
        favorites.remove(city.toLowerCase());
        log.info("Removed favorite city: {}", city);
    }

    public static List<FavoriteCity> getFavorites() {
        return new ArrayList<>(favorites.values());
    }

    public boolean isFavorite(String city) {
        return favorites.containsKey(city.toLowerCase());
    }
}
