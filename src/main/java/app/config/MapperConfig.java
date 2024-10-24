package app.config;


import app.dto.ReviewDTO;
import app.entity.Review;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

public class MapperConfig {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static ModelMapper getModelMapper() {
        //Set matching strategy
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //Or define custom mappings here if needed
        /*
        modelMapper.addMappings(new PropertyMap<Review, ReviewDTO>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setUsername(source.getUsername());
                map().setGameId(source.getGameId());
                map().setRating(source.getRating());
                map().setReview(source.getReview());
            }
        });
         */

        return modelMapper;
    }
}