package com.foodrescue.repository;

import com.foodrescue.model.FoodProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodProviderRepository extends JpaRepository<FoodProvider, Long> {
    Optional<FoodProvider> findByUser_Id(Long userId);

    /** Used to alert providers in the same city when an NGO/Volunteer posts a new event. */
    List<FoodProvider> findByCityIgnoreCase(String city);
}
