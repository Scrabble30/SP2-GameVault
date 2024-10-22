package app.services;

import app.dtos.GameDTO;

import java.util.Set;

public interface GameService {

    Set<GameDTO> getAllGames();

    Set<GameDTO> getAllGamesWithDetails(Set<GameDTO> gameDTOSet);

    GameDTO getGameById(Long id);

}
