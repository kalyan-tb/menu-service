package com.swiggy.menu.dto.request;

import com.swiggy.menu.ItemType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MenuItemRequest {
    @NotEmpty(message = "Dish name is required")
    private String dishName;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Availability is required")
    private Boolean availability;

    @NotNull(message = "Item type is required")
    private ItemType type;
}