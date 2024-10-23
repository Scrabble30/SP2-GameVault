package app.service;

import app.dto.GenreDTO;

import java.util.Set;

public interface GenreService {

    Set<GenreDTO> getGenres();

}
