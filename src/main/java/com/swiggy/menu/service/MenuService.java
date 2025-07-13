package com.swiggy.menu.service;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.MenuItemResponse;
import com.swiggy.menu.entity.MenuItem;
import com.swiggy.menu.entity.Restaurant;
import com.swiggy.menu.exception.ResourceNotFoundException;
import com.swiggy.menu.repository.MenuItemRepository;
import com.swiggy.menu.repository.RestaurantRepository;
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
    public Restaurant addRestaurant(CreateRestaurantRequest request) {
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

        return restaurantRepository.save(restaurant);
    }

    @Transactional(readOnly = true)
    public Restaurant getMenu(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    @Transactional
    public Restaurant updateMenu(Long restaurantId, List<MenuItemRequest> menuItems) {
        // first search into local cache
        // then search into distributed cache
        //then below code to search into db
        Restaurant existing = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

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
        /*existing.getMenuItems().clear();
        existing.getMenuItems().addAll(updatedItems);*/
        existing.setMenuItems(updatedItems);
        return restaurantRepository.save(existing);

        /*Restaurant existing = restaurantRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        existing.setMenuItems(request.getMenuItems());
        return restaurantRepository.save(existing);*/
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurantRepository.delete(restaurant);
        int a = 5;
        int b = 0 ;
        int c = (a/b);
        System.out.println(c);
    }

    /*@Transactional(readOnly = true)
    public List<MenuItem> searchMenuItemsByName(String dishname) {
        return menuItemRepository.findByDishNameContainingIgnoreCase(dishname);
    }*/

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
}