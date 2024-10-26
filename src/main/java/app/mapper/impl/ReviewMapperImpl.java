package app.mapper.impl;

import app.dto.ReviewDTO;
import app.entity.Review;
import app.mapper.AbstractMapper;

public class ReviewMapperImpl extends AbstractMapper<ReviewDTO, Review> {

    private static ReviewMapperImpl instance;

    private ReviewMapperImpl(Class<ReviewDTO> dtoClass, Class<Review> entityClass) {
        super(dtoClass, entityClass);
    }

    public static ReviewMapperImpl getInstance() {
        if (instance == null) {
            instance = new ReviewMapperImpl(ReviewDTO.class, Review.class);
        }

        return instance;
    }
}