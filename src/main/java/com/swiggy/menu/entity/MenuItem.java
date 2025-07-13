package com.swiggy.menu.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swiggy.menu.ItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "menu")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type; // VEG or NON_VEG

    @CreatedDate
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @LastModifiedDate
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @JsonBackReference
    private Restaurant restaurant;
}


