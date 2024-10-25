package app.config;

import app.dto.GameDTO;
import app.entity.Game;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;

public class MapperConfig {

    @Getter
    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<Game, GameDTO> gameMapper = modelMapper.createTypeMap(Game.class, GameDTO.class);
        gameMapper.addMapping(Game::getPlatformSet, GameDTO::setPlatformDTOSet);
        gameMapper.addMapping(Game::getGenreSet, GameDTO::setGenreDTOSet);

        TypeMap<GameDTO, Game> gameDTOMapper = modelMapper.createTypeMap(GameDTO.class, Game.class);
        gameDTOMapper.addMapping(GameDTO::getPlatformDTOSet, Game::setPlatformSet);
        gameDTOMapper.addMapping(GameDTO::getGenreDTOSet, Game::setGenreSet);
    }
}
