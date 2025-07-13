package com.swiggy.menu.dto.request;

import com.swiggy.menu.ItemType;
import lombok.Data;

@Data
public class MenuItemRequest {
    private String dishName;
    private Double price;
    private Boolean availability;
    private ItemType type;
}
