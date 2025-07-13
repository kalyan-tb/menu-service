package com.swiggy.menu.controller;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.MenuItemResponse;
import com.swiggy.menu.entity.Restaurant;
import com.swiggy.menu.service.MenuService;
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
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.ok(menuService.addRestaurant(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Restaurant> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    /*@PutMapping("/restaurant/{restaurantId}/menu")
    public ResponseEntity<Restaurant> updateMenu(@RequestBody Restaurant request) {
        return ResponseEntity.ok(menuService.updateMenu(request));
    }*/

    @PutMapping("/restaurant/{id}/menu")
    public ResponseEntity<Restaurant> updateMenu(
            @PathVariable("id") Long restaurantId,
            @RequestBody List<MenuItemRequest> menuItems) {
        return ResponseEntity.ok(menuService.updateMenu(restaurantId, menuItems));
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        menuService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    /*@GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenuItems(@RequestParam String dishname) {
        return ResponseEntity.ok(menuService.searchMenuItemsByName(dishname));
    }*/

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponse>> searchByDishName(@RequestParam String dishname) {
        return ResponseEntity.ok(menuService.searchByDishName(dishname));
    }
}