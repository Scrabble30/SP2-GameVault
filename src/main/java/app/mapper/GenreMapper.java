package app.mapper;

import app.dto.GenreDTO;
import app.entity.Genre;
import org.modelmapper.ModelMapper;

public class GenreMapper {

    private final ModelMapper modelMapper;

    public GenreMapper() {
        modelMapper = new ModelMapper();
    }

    public GenreDTO convertToDTO(Genre genre) {
        return modelMapper.map(genre, GenreDTO.class);
    }

    public Genre convertToEntity(GenreDTO genreDTO) {
        return modelMapper.map(genreDTO, Genre.class);
    }
}
