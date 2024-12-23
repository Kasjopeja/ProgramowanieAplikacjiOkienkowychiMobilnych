package com.example.Lab5.config;

import com.example.Lab5.AnimalRepository;
import com.example.Lab5.RatingService;
import com.example.Lab5.ShelterService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.example.Lab5.AnimalService;

@TestConfiguration
public class TestConfig {

    @Bean
    public AnimalService animalService() {
        return Mockito.mock(AnimalService.class);
    }

    @Bean
    public ShelterService shelterService() {
        return Mockito.mock(ShelterService.class);
    }

    @Bean
    public RatingService ratingService() {
        return Mockito.mock(RatingService.class);
    }
}
