package app.services;

import app.dtos.GenreDTO;

import java.util.Set;

public interface GenreService {

    Set<GenreDTO> getGenres();

}
