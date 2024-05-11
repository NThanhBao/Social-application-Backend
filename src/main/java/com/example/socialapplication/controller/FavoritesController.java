package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.FavoritesDto;
import com.example.socialapplication.service.FavoritesService;
import com.example.socialapplication.util.annotation.CheckLogin;
import com.example.socialapplication.util.exception.ConflictException;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @CheckLogin
    @PostMapping("/{postId}")
    public ResponseEntity<String> saveFavorite(@PathVariable String postId) {
        try {
            favoritesService.saveFavorite(postId);
            return ResponseEntity.ok("Lưu bài mình thích thành công!");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @CheckLogin
    @GetMapping("/all-posts")
    public ResponseEntity<List<FavoritesDto>> getFavoritesByToken(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
        Page<FavoritesDto> favoritesPage = favoritesService.getFavoritesByToken(pageable);
        return ResponseEntity.ok().body(favoritesPage.getContent());
    }

    @CheckLogin
    @DeleteMapping("/delete/{postsId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable String postsId) {
        try {
            favoritesService.deleteFavorite(postsId);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

}