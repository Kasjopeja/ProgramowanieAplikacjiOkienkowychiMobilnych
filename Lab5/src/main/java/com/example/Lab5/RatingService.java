package com.example.Lab5;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RatingService {

    RatingRepository ratingRepository;

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Iterable<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
