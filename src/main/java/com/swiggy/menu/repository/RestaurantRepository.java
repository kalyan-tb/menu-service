package com.swiggy.menu.repository;

import com.swiggy.menu.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
}
