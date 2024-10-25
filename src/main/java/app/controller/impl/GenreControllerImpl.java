package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.GenreDAOImpl;
import app.dto.GenreDTO;
import app.dto.PlatformDTO;
import app.entity.Genre;
import app.mapper.GenreMapper;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.MappingException;

import java.util.Set;
import java.util.stream.Collectors;

public class GenreControllerImpl implements Controller {

    private static GenreControllerImpl instance;

    private GenreDAOImpl genreDAO;
    private GenreMapper genreMapper;

    private GenreControllerImpl(EntityManagerFactory emf) {
        this.genreDAO = GenreDAOImpl.getInstance(emf);
        this.genreMapper = new GenreMapper();
    }

    public static GenreControllerImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new GenreControllerImpl(emf);
        }

        return instance;
    }

    @Override
    public void create(Context ctx) {
        try {
            GenreDTO genreDTO = ctx.bodyValidator(GenreDTO.class).get();
            Genre mappedGenre = genreMapper.convertToEntity(genreDTO);

            Genre createdGenre = genreDAO.create(mappedGenre);
            GenreDTO mappedGenreDTO = genreMapper.convertToDTO(createdGenre);

            ctx.status(HttpStatus.CREATED);
            ctx.json(mappedGenreDTO, GenreDTO.class);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        } catch (EntityExistsException e) {
            throw new ConflictResponse(e.getMessage());
        }
    }

    @Override
    public void getById(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            Genre genre = genreDAO.getById(id);
            GenreDTO mappedGenreDTO = genreMapper.convertToDTO(genre);

            ctx.status(HttpStatus.OK);
            ctx.json(mappedGenreDTO, PlatformDTO.class);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    @Override
    public void getAll(Context ctx) {
        try {
            Set<Genre> genreSet = genreDAO.getAll();
            Set<GenreDTO> mappedGenreDTOSet = genreSet.stream().map(genreMapper::convertToDTO).collect(Collectors.toSet());

            ctx.status(HttpStatus.OK);
            ctx.json(mappedGenreDTOSet, GenreDTO.class);
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            GenreDTO genreDTO = ctx.bodyValidator(GenreDTO.class).get();
            Genre mappedGenre = genreMapper.convertToEntity(genreDTO);

            Genre updatedGenre = genreDAO.update(id, mappedGenre);
            GenreDTO mappedGenreDTO = genreMapper.convertToDTO(updatedGenre);

            ctx.status(HttpStatus.OK);
            ctx.json(mappedGenreDTO, GenreDTO.class);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            genreDAO.delete(id);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }
}