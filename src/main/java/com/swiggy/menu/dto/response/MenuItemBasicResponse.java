package com.swiggy.menu.dto.response;

import com.swiggy.menu.ItemType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MenuItemBasicResponse {
    private Long id;
    private String dishName;
    private Double price;
    private Boolean availability;
    private ItemType type;
}
