package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.GameDAOImpl;
import app.dto.GameDTO;
import app.entity.Game;
import app.mapper.GameMapper;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.MappingException;

import java.util.Set;
import java.util.stream.Collectors;

public class GameControllerImpl implements Controller {

    private static GameControllerImpl instance;
    private final GameMapper gameMapper;
    private final GameDAOImpl gameDAO;

    private GameControllerImpl(EntityManagerFactory emf) {
        this.gameDAO = GameDAOImpl.getInstance(emf);
        this.gameMapper = new GameMapper();
    }

    public static GameControllerImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new GameControllerImpl(emf);
        }

        return instance;
    }

    @Override
    public void create(Context ctx) {
        try {
            GameDTO gameDTO = ctx.bodyValidator(GameDTO.class).get();
            Game game = gameMapper.convertToEntity(gameDTO);

            Game createdGame = gameDAO.create(game);
            GameDTO createdGameDTO = gameMapper.convertToDTO(createdGame);

            ctx.status(HttpStatus.CREATED);
            ctx.json(createdGameDTO, GameDTO.class);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        } catch (EntityExistsException e) {
            throw new ConflictResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    @Override
    public void getById(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            Game game = gameDAO.getById(id);
            GameDTO gameDTO = gameMapper.convertToDTO(game);

            ctx.status(HttpStatus.OK);
            ctx.json(gameDTO, GameDTO.class);
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
            Set<Game> games = gameDAO.getAll();
            Set<GameDTO> gameDTOSet = games.stream().map(gameMapper::convertToDTO).collect(Collectors.toSet());

            ctx.status(HttpStatus.OK);
            ctx.json(gameDTOSet, GameDTO.class);
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            GameDTO gameDTO = ctx.bodyValidator(GameDTO.class).get();
            Game game = gameMapper.convertToEntity(gameDTO);

            Game updatedGame = gameDAO.update(id, game);
            GameDTO updatedGameDTO = gameMapper.convertToDTO(updatedGame);

            ctx.status(HttpStatus.OK);
            ctx.json(updatedGameDTO, GameDTO.class);
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

            gameDAO.delete(id);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    public void getAllReviews(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            ctx.redirect(String.format("/api/reviews?gameId=%d", id));
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        }
    }
}
