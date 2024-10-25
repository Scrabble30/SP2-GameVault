package app.mapper;

import app.config.MapperConfig;
import app.dto.GameDTO;
import app.entity.Game;

public class GameMapper {

    public GameDTO convertToDTO(Game game) {
        return MapperConfig.getModelMapper().map(game, GameDTO.class);
    }

    public Game convertToEntity(GameDTO gameDTO) {
        return MapperConfig.getModelMapper().map(gameDTO, Game.class);
    }
}
