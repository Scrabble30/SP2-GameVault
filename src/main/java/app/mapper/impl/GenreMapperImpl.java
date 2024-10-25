package app.mapper.impl;

import app.dto.GenreDTO;
import app.entity.Genre;
import app.mapper.AbstractMapper;

public class GenreMapperImpl extends AbstractMapper<GenreDTO, Genre> {
    private static GenreMapperImpl instance;

    private GenreMapperImpl(Class<GenreDTO> dtoClass, Class<Genre> entityClass) {
        super(dtoClass, entityClass);
    }

    public static GenreMapperImpl getInstance() {
        if (instance == null) {
            instance = new GenreMapperImpl(GenreDTO.class, Genre.class);
        }

        return instance;
    }
}
