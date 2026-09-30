package com.foodrescue.repository;

import com.foodrescue.model.FoodProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodProviderRepository extends JpaRepository<FoodProvider, Long> {
    Optional<FoodProvider> findByUser_Id(Long userId);
}
