package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.PlatformDAOImpl;
import app.dto.PlatformDTO;
import app.entity.Platform;
import app.mapper.impl.PlatformMapperImpl;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.MappingException;

import java.util.Set;
import java.util.stream.Collectors;

public class PlatformControllerImpl implements Controller {

    private static PlatformControllerImpl instance;

    private final PlatformMapperImpl platformMapper;
    private final PlatformDAOImpl platformDAO;

    private PlatformControllerImpl(EntityManagerFactory emf) {
        this.platformDAO = PlatformDAOImpl.getInstance(emf);
        this.platformMapper = PlatformMapperImpl.getInstance();
    }

    public static PlatformControllerImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new PlatformControllerImpl(emf);
        }

        return instance;
    }

    @Override
    public void create(Context ctx) {
        try {
            PlatformDTO platformDTO = ctx.bodyValidator(PlatformDTO.class).get();
            Platform platform = platformMapper.convertToEntity(platformDTO);

            Platform createdPlatform = platformDAO.create(platform);
            PlatformDTO createdPlatformDTO = platformMapper.convertToDTO(createdPlatform);

            ctx.status(HttpStatus.CREATED);
            ctx.json(createdPlatformDTO, PlatformDTO.class);
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

            Platform platform = platformDAO.getById(id);
            PlatformDTO platformDTO = platformMapper.convertToDTO(platform);

            ctx.status(HttpStatus.OK);
            ctx.json(platformDTO, PlatformDTO.class);
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
            Set<Platform> platforms = platformDAO.getAll();
            Set<PlatformDTO> platformDTOList = platforms.stream().map(platformMapper::convertToDTO).collect(Collectors.toSet());

            ctx.status(HttpStatus.OK);
            ctx.json(platformDTOList, PlatformDTO.class);
        } catch (MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            PlatformDTO platformDTO = ctx.bodyValidator(PlatformDTO.class).get();
            Platform platform = platformMapper.convertToEntity(platformDTO);

            Platform updatedPlatform = platformDAO.update(id, platform);
            PlatformDTO updatedPlatformDTO = platformMapper.convertToDTO(updatedPlatform);

            ctx.status(HttpStatus.OK);
            ctx.json(updatedPlatformDTO, PlatformDTO.class);
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

            platformDAO.delete(id);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }
}
