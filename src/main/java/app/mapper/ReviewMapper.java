package app.mapper;

import app.config.MapperConfig;
import app.dto.ReviewDTO;
import app.entity.Review;

public class ReviewMapper {

    public ReviewDTO convertToDTO(Review review) {
        return MapperConfig.getModelMapper().map(review, ReviewDTO.class);
    }

    public Review convertToEntity(ReviewDTO reviewDTO) {
        return MapperConfig.getModelMapper().map(reviewDTO, Review.class);
    }
}