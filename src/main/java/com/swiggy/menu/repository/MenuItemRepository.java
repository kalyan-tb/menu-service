package com.swiggy.menu.repository;


import com.swiggy.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByDishNameContainingIgnoreCase(String dishName);
}
