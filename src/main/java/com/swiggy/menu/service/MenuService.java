package com.swiggy.menu.service;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.*;
import com.swiggy.menu.entity.MenuItem;
import com.swiggy.menu.entity.Restaurant;
import com.swiggy.menu.exception.ResourceNotFoundException;
import com.swiggy.menu.repository.MenuItemRepository;
import com.swiggy.menu.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public RestaurantResponse addRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPincode(request.getPincode());

        List<MenuItem> items = request.getMenuItems().stream().map(dto -> {
            MenuItem item = new MenuItem();
            item.setDishName(dto.getDishName());
            item.setPrice(dto.getPrice());
            item.setAvailability(dto.getAvailability());
            item.setType(dto.getType());
            item.setRestaurant(restaurant);
            return item;
        }).collect(Collectors.toList());

        restaurant.setMenuItems(items);
        Restaurant saved = restaurantRepository.save(restaurant);


        // Manual mapping to response DTO
        RestaurantResponse response = new RestaurantResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setAddress(saved.getAddress());
        response.setCity(saved.getCity());
        response.setPincode(saved.getPincode());

        return response;
    }


    @Transactional(readOnly = true)
    public PaginatedMenuItemResponse getMenu(Long restaurantId, int page, int size) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MenuItem> pagedItems = menuItemRepository.findByRestaurant(restaurant, pageable);

        List<MenuItemBasicResponse> itemResponses = pagedItems.getContent().stream().map(item -> {
            MenuItemBasicResponse dto = new MenuItemBasicResponse();
            dto.setId(item.getId());
            dto.setDishName(item.getDishName());
            dto.setPrice(item.getPrice());
            dto.setAvailability(item.getAvailability());
            dto.setType(item.getType());
            return dto;
        }).toList();

        PaginatedMenuItemResponse response = new PaginatedMenuItemResponse();
        response.setRestaurantId(restaurant.getId());
        response.setRestaurantName(restaurant.getName());
        response.setMenuItems(itemResponses);
        response.setCurrentPage(pagedItems.getNumber());
        response.setTotalPages(pagedItems.getTotalPages());
        response.setTotalElements(pagedItems.getTotalElements());

        return response;
    }

    @Transactional
    public RestaurantMenuResponse updateMenu(Long restaurantId, List<MenuItemRequest> menuItems) {
        // first search into local cache
        // then search into distributed cache
        //then below code to search into db
        Restaurant existing = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        try{
            List<MenuItem> updatedItems = menuItems.stream().map(menu -> {
                MenuItem item = new MenuItem();
                item.setDishName(menu.getDishName());
                item.setPrice(menu.getPrice());
                item.setAvailability(menu.getAvailability());
                item.setType(menu.getType());
                item.setRestaurant(existing);
                return item;
            }).toList();

            // 👇 Do this instead of setMenuItems(...) to avoid Hibernate orphan issues
            existing.getMenuItems().clear();
            existing.getMenuItems().addAll(updatedItems);
            //existing.setMenuItems(updatedItems);
            Restaurant saved = restaurantRepository.save(existing);
            return toRestaurantMenuResponse(saved);
        } catch (Exception e){
            throw new RuntimeException("Failed to update menu: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurantRepository.delete(restaurant);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchByDishName(String dishName) {
        List<MenuItem> items = menuItemRepository.findByDishNameContainingIgnoreCase(dishName);
        return items.stream().map(item -> {
            MenuItemResponse dto = new MenuItemResponse();
            dto.setId(item.getId());
            dto.setDishName(item.getDishName());
            dto.setPrice(item.getPrice());
            dto.setAvailability(item.getAvailability());
            dto.setType(item.getType());
            dto.setRestaurantId(item.getRestaurant().getId());
            dto.setRestaurantName(item.getRestaurant().getName());
            return dto;
        }).toList();
    }

    private RestaurantMenuResponse toRestaurantMenuResponse(Restaurant restaurant) {
        RestaurantMenuResponse response = new RestaurantMenuResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());

        List<MenuItemBasicResponse> menuItemResponses = restaurant.getMenuItems().stream().map(item -> {
            MenuItemBasicResponse dto = new MenuItemBasicResponse();
            dto.setId(item.getId());
            dto.setDishName(item.getDishName());
            dto.setPrice(item.getPrice());
            dto.setAvailability(item.getAvailability());
            dto.setType(item.getType());
            return dto;
        }).toList();

        response.setMenuItems(menuItemResponses);
        return response;
    }

}