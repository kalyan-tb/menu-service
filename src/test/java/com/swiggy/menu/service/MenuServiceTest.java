package com.swiggy.menu.service;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.*;
import com.swiggy.menu.entity.MenuItem;
import com.swiggy.menu.entity.Restaurant;
import com.swiggy.menu.exception.ResourceNotFoundException;
import com.swiggy.menu.repository.MenuItemRepository;
import com.swiggy.menu.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuService menuService;

    private Restaurant restaurant;
    private CreateRestaurantRequest createRequest;
    private MenuItemRequest menuItemRequest;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Test St");
        restaurant.setCity("Test City");
        restaurant.setPincode("123456");

        menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setDishName("Pizza");
        menuItem.setPrice(10.99);
        menuItem.setAvailability(true);
        menuItem.setType(com.swiggy.menu.ItemType.VEG);
        menuItem.setRestaurant(restaurant);

        menuItemRequest = new MenuItemRequest();
        menuItemRequest.setDishName("Pizza");
        menuItemRequest.setPrice(10.99);
        menuItemRequest.setAvailability(true);
        menuItemRequest.setType(com.swiggy.menu.ItemType.VEG);

        createRequest = new CreateRestaurantRequest();
        createRequest.setName("Test Restaurant");
        createRequest.setAddress("123 Test St");
        createRequest.setCity("Test City");
        createRequest.setPincode("123456");
        createRequest.setMenuItems(Arrays.asList(menuItemRequest));
    }

    @Test
    void addRestaurant_success() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponse response = menuService.addRestaurant(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Restaurant", response.getName());
        assertEquals("123 Test St", response.getAddress());
        assertEquals("Test City", response.getCity());
        assertEquals("123456", response.getPincode());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void addRestaurant_emptyMenuItems_success() {
        createRequest.setMenuItems(Collections.emptyList());
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponse response = menuService.addRestaurant(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Restaurant", response.getName());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void getMenu_success() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<MenuItem> page = new PageImpl<>(Arrays.asList(menuItem));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByRestaurant(any(Restaurant.class), any(PageRequest.class))).thenReturn(page);

        PaginatedMenuItemResponse response = menuService.getMenuByRestaurantId(1L, 0, 10);

        assertNotNull(response);
        assertEquals(1L, response.getRestaurantId());
        assertEquals("Test Restaurant", response.getRestaurantName());
        assertEquals(1, response.getMenuItems().size());
        assertEquals("Pizza", response.getMenuItems().get(0).getDishName());
        assertEquals(0, response.getCurrentPage());
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getTotalElements());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).findByRestaurant(any(Restaurant.class), any(PageRequest.class));
    }

    @Test
    void getMenu_restaurantNotFound_throwsException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenuByRestaurantId(1L, 0, 10));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuItemRepository, never()).findByRestaurant(any(), any());
    }

    @Test
    void getMenu_emptyPage_success() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<MenuItem> emptyPage = new PageImpl<>(Collections.emptyList());
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByRestaurant(any(Restaurant.class), any(PageRequest.class))).thenReturn(emptyPage);

        PaginatedMenuItemResponse response = menuService.getMenuByRestaurantId(1L, 0, 10);

        assertNotNull(response);
        assertEquals(1L, response.getRestaurantId());
        assertEquals("Test Restaurant", response.getRestaurantName());
        assertTrue(response.getMenuItems().isEmpty());
        assertEquals(0, response.getCurrentPage());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getTotalElements());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuItemRepository, times(1)).findByRestaurant(any(Restaurant.class), any(PageRequest.class));
    }

    @Test
    void updateMenu_success() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantMenuResponse response = menuService.updateMenu(1L, Arrays.asList(menuItemRequest));

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Restaurant", response.getName());
        assertEquals(1, response.getMenuItems().size());
        assertEquals("Pizza", response.getMenuItems().get(0).getDishName());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void updateMenu_emptyMenuItems_success() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantMenuResponse response = menuService.updateMenu(1L, Collections.emptyList());

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Restaurant", response.getName());
        assertTrue(response.getMenuItems().isEmpty());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void updateMenu_restaurantNotFound_throwsException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.updateMenu(1L, Arrays.asList(menuItemRequest)));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void deleteRestaurant_success() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        menuService.deleteRestaurant(1L);

        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).delete(restaurant);
    }

    @Test
    void deleteRestaurant_notFound_throwsException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.deleteRestaurant(1L));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).delete(any());
    }

    @Test
    void searchByDishName_success() {
        when(menuItemRepository.findByDishNameContainingIgnoreCase("Pizza")).thenReturn(Arrays.asList(menuItem));

        List<MenuItemResponse> response = menuService.searchByDishName("Pizza");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Pizza", response.get(0).getDishName());
        assertEquals(1L, response.get(0).getRestaurantId());
        assertEquals("Test Restaurant", response.get(0).getRestaurantName());
        verify(menuItemRepository, times(1)).findByDishNameContainingIgnoreCase("Pizza");
    }

    @Test
    void searchByDishName_noResults_returnsEmptyList() {
        when(menuItemRepository.findByDishNameContainingIgnoreCase("Burger")).thenReturn(Collections.emptyList());

        List<MenuItemResponse> response = menuService.searchByDishName("Burger");

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(menuItemRepository, times(1)).findByDishNameContainingIgnoreCase("Burger");
    }
}