package com.swiggy.menu.dto.response;

import com.swiggy.menu.ItemType;
import lombok.Data;

@Data
public class MenuItemResponse {
    private Long id;
    private String dishName;
    private Double price;
    private Boolean availability;
    private ItemType type;
    private Long restaurantId;
    private String restaurantName;
}

