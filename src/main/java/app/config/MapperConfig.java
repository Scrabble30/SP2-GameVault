package app.config;

import app.dto.GameDTO;
import app.dto.GamePageDTO;
import app.entity.Game;
import app.filter.GamePage;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;

@Getter
public class MapperConfig {

    private static MapperConfig instance;
    private final ModelMapper modelMapper;

    private MapperConfig() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<Game, GameDTO> gameMapper = modelMapper.createTypeMap(Game.class, GameDTO.class);
        gameMapper.addMapping(Game::getPlatformSet, GameDTO::setPlatformDTOSet);
        gameMapper.addMapping(Game::getGenreSet, GameDTO::setGenreDTOSet);

        TypeMap<GameDTO, Game> gameDTOMapper = modelMapper.createTypeMap(GameDTO.class, Game.class);
        gameDTOMapper.addMapping(GameDTO::getPlatformDTOSet, Game::setPlatformSet);
        gameDTOMapper.addMapping(GameDTO::getGenreDTOSet, Game::setGenreSet);

        TypeMap<GamePage, GamePageDTO> gamePageMapper = modelMapper.createTypeMap(GamePage.class, GamePageDTO.class);
        gamePageMapper.addMapping(GamePage::getGameSet, GamePageDTO::setGameDTOSet);
    }

    public static MapperConfig getInstance() {
        if (instance == null) {
            instance = new MapperConfig();
        }

        return instance;
    }
}
