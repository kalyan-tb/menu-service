package com.swiggy.menu.controller;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.MenuItemResponse;
import com.swiggy.menu.dto.response.PaginatedMenuItemResponse;
import com.swiggy.menu.dto.response.RestaurantMenuResponse;
import com.swiggy.menu.dto.response.RestaurantResponse;
import com.swiggy.menu.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantResponse> addRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.ok(menuService.addRestaurant(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<PaginatedMenuItemResponse> getMenu(@PathVariable Long restaurantId,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(menuService.getMenuByRestaurantId(restaurantId, page, size));
    }

    @PutMapping("/restaurant/{id}/menu")
    public ResponseEntity<RestaurantMenuResponse> updateMenu(
            @PathVariable("id") Long restaurantId,
            @Valid @RequestBody List<MenuItemRequest> menuItems) {
        return ResponseEntity.ok(menuService.updateMenu(restaurantId, menuItems));
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        menuService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponse>> searchByDishName(@RequestParam String dishname) {
        return ResponseEntity.ok(menuService.searchByDishName(dishname));
    }
}