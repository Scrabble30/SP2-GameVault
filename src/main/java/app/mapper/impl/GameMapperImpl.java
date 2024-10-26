package app.mapper.impl;

import app.dto.GameDTO;
import app.entity.Game;
import app.mapper.AbstractMapper;

public class GameMapperImpl extends AbstractMapper<GameDTO, Game> {

    private static GameMapperImpl instance;

    private GameMapperImpl(Class<GameDTO> dtoClass, Class<Game> entityClass) {
        super(dtoClass, entityClass);
    }

    public static GameMapperImpl getInstance() {
        if (instance == null) {
            instance = new GameMapperImpl(GameDTO.class, Game.class);
        }

        return instance;
    }
}