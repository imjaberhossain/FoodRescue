package com.foodrescue.repository;

import com.foodrescue.model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findAllByOrderByNameAsc();
}
