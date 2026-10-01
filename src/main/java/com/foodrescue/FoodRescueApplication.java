package com.foodrescue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling // needed for the automatic "expire old listings" job
public class FoodRescueApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodRescueApplication.class, args);
    }

    /** Used to hash passwords, so we never store plain-text passwords. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
