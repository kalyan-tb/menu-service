package com.swiggy.menu.repository;


import com.swiggy.menu.entity.MenuItem;
import com.swiggy.menu.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByDishNameContainingIgnoreCase(String dishName);

    //added for pagination
    Page<MenuItem> findByRestaurant(Restaurant restaurant, Pageable pageable);

}
