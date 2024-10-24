package app.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class MapperConfig {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static ModelMapper getModelMapper() {
        return (ModelMapper) modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
}