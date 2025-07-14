package com.swiggy.menu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateRestaurantRequest {
    @NotEmpty(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name must not exceed 100 characters")
    private String name;

    @NotEmpty(message = "Restaurant address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotEmpty(message = "Pin code is required")
    @Size(min = 6, max = 6, message = "Pincode must be exactly 6 characters")
    private String pincode;

    @NotEmpty
    private List<@Valid MenuItemRequest> menuItems;
}