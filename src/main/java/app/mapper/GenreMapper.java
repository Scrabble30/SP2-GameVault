package app.mapper;

import app.config.MapperConfig;
import app.dto.GenreDTO;
import app.entity.Genre;

public class GenreMapper {

    public GenreDTO convertToDTO(Genre genre) {
        return MapperConfig.getModelMapper().map(genre, GenreDTO.class);
    }

    public Genre convertToEntity(GenreDTO genreDTO) {
        return MapperConfig.getModelMapper().map(genreDTO, Genre.class);
    }
}
