package app.service;

import app.dto.GameDTO;

import java.util.Set;

public interface GameService {

    Set<GameDTO> getAllGames();

    Set<GameDTO> getAllGamesWithDetails(Set<GameDTO> gameDTOSet);

    GameDTO getGameById(Long id);

}
