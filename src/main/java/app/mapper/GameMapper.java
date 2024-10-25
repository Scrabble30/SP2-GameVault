package app.mapper;

import app.config.MapperConfig;
import app.dto.GameDTO;
import app.entity.Game;
import org.modelmapper.ModelMapper;

public class GameMapper {

    private final ModelMapper modelMapper;

    public GameMapper() {
        this.modelMapper = MapperConfig.getInstance().getModelMapper();
    }

    public GameDTO convertToDTO(Game game) {
        return modelMapper.map(game, GameDTO.class);
    }

    public Game convertToEntity(GameDTO gameDTO) {
        return modelMapper.map(gameDTO, Game.class);
    }
}
