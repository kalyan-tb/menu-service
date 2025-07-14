package com.swiggy.menu.dto.response;

import com.swiggy.menu.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemBasicResponse {
    private Long id;
    private String dishName;
    private Double price;
    private Boolean availability;
    private ItemType type;
}
