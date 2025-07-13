package com.swiggy.menu.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateRestaurantRequest {
    private String name;
    private String address;
    private String city;
    private String pincode;
    private List<MenuItemRequest> menuItems;
}
