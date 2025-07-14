package com.swiggy.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedMenuItemResponse {
    private Long restaurantId;
    private String restaurantName;
    private List<MenuItemBasicResponse> menuItems;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
