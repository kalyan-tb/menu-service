package com.swiggy.menu.controller;

import com.swiggy.menu.dto.request.CreateRestaurantRequest;
import com.swiggy.menu.dto.request.MenuItemRequest;
import com.swiggy.menu.dto.response.MenuItemResponse;
import com.swiggy.menu.dto.response.PaginatedMenuItemResponse;
import com.swiggy.menu.dto.response.RestaurantMenuResponse;
import com.swiggy.menu.dto.response.RestaurantResponse;
import com.swiggy.menu.service.MenuService;
import com.swiggy.menu.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private CreateRestaurantRequest createRequest;
    private RestaurantResponse restaurantResponse;
    private MenuItemRequest menuItemRequest;
    private PaginatedMenuItemResponse paginatedResponse;
    private RestaurantMenuResponse restaurantMenuResponse;
    private MenuItemResponse menuItemResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateRestaurantRequest();
        createRequest.setName("Test Restaurant");
        createRequest.setAddress("123 Test St");
        createRequest.setCity("Test City");
        createRequest.setPincode("123456");

        menuItemRequest = new MenuItemRequest();
        menuItemRequest.setDishName("Pizza");
        menuItemRequest.setPrice(10.99);
        menuItemRequest.setAvailability(true);
        menuItemRequest.setType(com.swiggy.menu.ItemType.VEG);
        createRequest.setMenuItems(Arrays.asList(menuItemRequest));

        restaurantResponse = new RestaurantResponse();
        restaurantResponse.setId(1L);
        restaurantResponse.setName("Test Restaurant");
        restaurantResponse.setAddress("123 Test St");
        restaurantResponse.setCity("Test City");
        restaurantResponse.setPincode("123456");

        paginatedResponse = new PaginatedMenuItemResponse();
        paginatedResponse.setRestaurantId(1L);
        paginatedResponse.setRestaurantName("Test Restaurant");
        paginatedResponse.setCurrentPage(0);
        paginatedResponse.setTotalPages(1);
        paginatedResponse.setTotalElements(1);

        restaurantMenuResponse = new RestaurantMenuResponse();
        restaurantMenuResponse.setId(1L);
        restaurantMenuResponse.setName("Test Restaurant");

        menuItemResponse = new MenuItemResponse();
        menuItemResponse.setId(1L);
        menuItemResponse.setDishName("Pizza");
        menuItemResponse.setPrice(10.99);
        menuItemResponse.setAvailability(true);
        menuItemResponse.setType(com.swiggy.menu.ItemType.VEG);
        menuItemResponse.setRestaurantId(1L);
        menuItemResponse.setRestaurantName("Test Restaurant");
    }

    @Test
    void addRestaurant_success() {
        when(menuService.addRestaurant(any(CreateRestaurantRequest.class))).thenReturn(restaurantResponse);

        ResponseEntity<RestaurantResponse> response = menuController.addRestaurant(createRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Restaurant", response.getBody().getName());
        verify(menuService, times(1)).addRestaurant(any(CreateRestaurantRequest.class));
    }

    @Test
    void getMenu_success() {
        when(menuService.getMenuByRestaurantId(eq(1L), eq(0), eq(10))).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedMenuItemResponse> response = menuController.getMenu(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getRestaurantId());
        assertEquals("Test Restaurant", response.getBody().getRestaurantName());
        assertEquals(0, response.getBody().getCurrentPage());
        verify(menuService, times(1)).getMenuByRestaurantId(1L, 0, 10);
    }

    @Test
    void getMenu_restaurantNotFound_throwsException() {
        when(menuService.getMenuByRestaurantId(eq(1L), eq(0), eq(10))).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        assertThrows(ResourceNotFoundException.class, () -> menuController.getMenu(1L, 0, 10));
        verify(menuService, times(1)).getMenuByRestaurantId(1L, 0, 10);
    }

    @Test
    void updateMenu_success() {
        when(menuService.updateMenu(eq(1L), anyList())).thenReturn(restaurantMenuResponse);

        ResponseEntity<RestaurantMenuResponse> response = menuController.updateMenu(1L, Arrays.asList(menuItemRequest));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Restaurant", response.getBody().getName());
        verify(menuService, times(1)).updateMenu(eq(1L), anyList());
    }

    @Test
    void updateMenu_restaurantNotFound_throwsException() {
        when(menuService.updateMenu(eq(1L), anyList())).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        assertThrows(ResourceNotFoundException.class, () -> menuController.updateMenu(1L, Arrays.asList(menuItemRequest)));
        verify(menuService, times(1)).updateMenu(eq(1L), anyList());
    }

    @Test
    void deleteRestaurant_success() {
        doNothing().when(menuService).deleteRestaurant(1L);

        ResponseEntity<Void> response = menuController.deleteRestaurant(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(menuService, times(1)).deleteRestaurant(1L);
    }

    @Test
    void deleteRestaurant_notFound_throwsException() {
        doThrow(new ResourceNotFoundException("Restaurant not found")).when(menuService).deleteRestaurant(1L);

        assertThrows(ResourceNotFoundException.class, () -> menuController.deleteRestaurant(1L));
        verify(menuService, times(1)).deleteRestaurant(1L);
    }

    @Test
    void searchByDishName_success() {
        when(menuService.searchByDishName("Pizza")).thenReturn(Arrays.asList(menuItemResponse));

        ResponseEntity<List<MenuItemResponse>> response = menuController.searchByDishName("Pizza");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Pizza", response.getBody().get(0).getDishName());
        verify(menuService, times(1)).searchByDishName("Pizza");
    }
}