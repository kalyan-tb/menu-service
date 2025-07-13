package com.swiggy.menu.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantMenuResponse {
    private Long id;
    private String name;
    private List<MenuItemBasicResponse> menuItems;
}