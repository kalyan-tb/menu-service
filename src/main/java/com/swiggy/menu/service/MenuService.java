package com.swiggy.menu.service;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.*;
import com.swiggy.menu.entity.MenuItem;
import com.swiggy.menu.entity.Restaurant;
import com.swiggy.menu.exception.ResourceNotFoundException;
import com.swiggy.menu.exception.ValidationError;
import com.swiggy.menu.repository.MenuItemRepository;
import com.swiggy.menu.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public RestaurantResponse addRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = mapToRestaurant(request);
        restaurant.setMenuItems(mapToMenuItems(request.getMenuItems(), restaurant));

        Restaurant saved = saveWithErrorHandling(restaurant, "Failed to save restaurant");
        logger.info("Restaurant saved successfully: ID={}", saved.getId());

        return mapToRestaurantResponse(saved);
    }


    @Transactional(readOnly = true)
    public PaginatedMenuItemResponse getMenuByRestaurantId(Long restaurantId, int page, int size) {
        // first search into local/application cache if found then return else proceed (cache miss)
        // then search into distributed cache (redis) if found then return else proceed
        //Final call will be to search in db and update the local and distributed cache for eventual consistency
        Restaurant restaurant = findRestaurantById(restaurantId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MenuItem> pagedItems = menuItemRepository.findByRestaurant(restaurant, pageable);

        return mapToPaginatedResponse(restaurant, pagedItems);
    }

    @Transactional
    public RestaurantMenuResponse updateMenu(Long restaurantId, List<MenuItemRequest> menuItems) {

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.getMenuItems().clear();
        restaurant.getMenuItems().addAll(mapToMenuItems(menuItems, restaurant));

        Restaurant saved = saveWithErrorHandling(restaurant, "Failed to update menu");
        return mapToRestaurantMenuResponse(saved);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        try {
            restaurantRepository.delete(restaurant);
            logger.info("Restaurant deleted successfully: ID={}", restaurantId);
        } catch (DataAccessException e) {
            logger.error("Database error deleting restaurant: {}", e.getMessage(), e);
            throw new RuntimeException(ValidationError.DATABASE_ERROR.errorMessage, e);
        }
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchByDishName(String dishName) {
        List<MenuItem> items = menuItemRepository.findByDishNameContainingIgnoreCase(dishName);
        return items.stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    // Helper Methods

    private Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    private Restaurant mapToRestaurant(CreateRestaurantRequest request) {
        return Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .menuItems(new ArrayList<>())
                .build();
    }

    private List<MenuItem> mapToMenuItems(List<MenuItemRequest> requests, Restaurant restaurant) {
        if (requests == null) {
            return new ArrayList<>();
        }
        return requests.stream()
                .map(dto -> MenuItem.builder()
                        .dishName(dto.getDishName())
                        .price(dto.getPrice())
                        .availability(dto.getAvailability())
                        .type(dto.getType())
                        .restaurant(restaurant)
                        .build())
                .collect(Collectors.toList());
    }

    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .pincode(restaurant.getPincode())
                .build();
    }

    private RestaurantMenuResponse mapToRestaurantMenuResponse(Restaurant restaurant) {
        RestaurantMenuResponse response = RestaurantMenuResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .build();

        List<MenuItemBasicResponse> menuItemResponses = restaurant.getMenuItems().stream()
                .map(this::mapToMenuItemBasicResponse)
                .collect(Collectors.toList());
        response.setMenuItems(menuItemResponses);
        return response;
    }

    private PaginatedMenuItemResponse mapToPaginatedResponse(Restaurant restaurant, Page<MenuItem> pagedItems) {
        PaginatedMenuItemResponse response = PaginatedMenuItemResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .currentPage(pagedItems.getNumber())
                .totalPages(pagedItems.getTotalPages())
                .totalElements(pagedItems.getTotalElements())
                .build();

        List<MenuItemBasicResponse> itemResponses = pagedItems.getContent().stream()
                .map(this::mapToMenuItemBasicResponse)
                .collect(Collectors.toList());
        response.setMenuItems(itemResponses);
        return response;
    }

    private MenuItemBasicResponse mapToMenuItemBasicResponse(MenuItem item) {
        return MenuItemBasicResponse.builder()
                .id(item.getId())
                .dishName(item.getDishName())
                .price(item.getPrice())
                .availability(item.getAvailability())
                .type(item.getType())
                .build();
    }

    private MenuItemResponse mapToMenuItemResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .dishName(item.getDishName())
                .price(item.getPrice())
                .availability(item.getAvailability())
                .type(item.getType())
                .restaurantId(item.getRestaurant().getId())
                .restaurantName(item.getRestaurant().getName())
                .build();
    }

    private <T> T saveWithErrorHandling(T entity, String errorMessage) {
        try {
            if (entity instanceof Restaurant) {
                return (T) restaurantRepository.save((Restaurant) entity);
            } else {
                throw new IllegalArgumentException("Unsupported entity type");
            }
        } catch (DataAccessException e) {
            logger.error("{}: {}", errorMessage, e.getMessage(), e);
            throw new RuntimeException(ValidationError.DATABASE_ERROR.errorMessage, e);
        }
    }

}