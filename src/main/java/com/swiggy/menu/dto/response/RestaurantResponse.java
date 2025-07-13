package com.swiggy.menu.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String pincode;
}
