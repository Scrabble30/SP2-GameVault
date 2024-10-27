package app.mapper;

import app.config.MapperConfig;
import app.dto.GamePageDTO;
import app.filter.GamePage;
import org.modelmapper.ModelMapper;

public class GamePageMapper {

    private static GamePageMapper instance;
    private final ModelMapper modelMapper;

    private GamePageMapper() {
        modelMapper = MapperConfig.getInstance().getModelMapper();
    }

    public static GamePageMapper getInstance() {
        if (instance == null) {
            instance = new GamePageMapper();
        }

        return instance;
    }

    public GamePageDTO convertToDTO(GamePage gamePage) {
        return modelMapper.map(gamePage, GamePageDTO.class);
    }
}
