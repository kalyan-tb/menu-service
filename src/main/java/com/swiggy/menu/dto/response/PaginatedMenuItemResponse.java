package com.swiggy.menu.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedMenuItemResponse {
    private Long restaurantId;
    private String restaurantName;
    private List<MenuItemBasicResponse> menuItems;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
