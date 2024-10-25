package app.mapper;

import app.config.MapperConfig;
import app.dto.ReviewDTO;
import app.entity.Review;
import org.modelmapper.ModelMapper;

public class ReviewMapper {

    private final ModelMapper modelMapper;

    public ReviewMapper() {
        modelMapper = MapperConfig.getInstance().getModelMapper();
    }

    public ReviewDTO convertToDTO(Review review) {
        return modelMapper.map(review, ReviewDTO.class);
    }

    public Review convertToEntity(ReviewDTO reviewDTO) {
        return modelMapper.map(reviewDTO, Review.class);
    }
}