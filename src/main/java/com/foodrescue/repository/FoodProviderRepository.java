package com.foodrescue.repository;

import com.foodrescue.model.FoodProvider;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
import java.util.Optional;

public interface FoodProviderRepository extends JpaRepository<FoodProvider, Long> {
    Optional<FoodProvider> findByUser_Id(Long userId);
<<<<<<< HEAD

    /** Used to alert providers in the same city when an NGO/Volunteer posts a new event. */
    List<FoodProvider> findByCityIgnoreCase(String city);
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
}
