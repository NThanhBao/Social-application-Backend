package com.example.socialapplication.service;

import com.example.socialapplication.model.dto.FavoritesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritesService {

    void saveFavorite(String posts);

    void deleteFavorite(String posts);

    Page<FavoritesDto> getFavoritesByToken(Pageable pageable);

    boolean checkFavoriteStatus(String postId);
}