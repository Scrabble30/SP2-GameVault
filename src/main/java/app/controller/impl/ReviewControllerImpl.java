package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.ReviewDAOImpl;
import app.dto.ReviewDTO;
import app.entity.Review;
import app.mapper.ReviewMapper;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.MappingException;

import java.util.Set;
import java.util.stream.Collectors;

public class ReviewControllerImpl implements Controller {

    private static ReviewControllerImpl instance;

    private final ReviewDAOImpl reviewDAO;
    private final ReviewMapper reviewMapper;

    private ReviewControllerImpl(EntityManagerFactory emf) {
        this.reviewDAO = ReviewDAOImpl.getInstance(emf);
        this.reviewMapper = new ReviewMapper();
    }

    public static ReviewControllerImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new ReviewControllerImpl(emf);
        }

        return instance;
    }

    @Override
    public void getById(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            Review foundReview = reviewDAO.getById(id);
            ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(foundReview);

            ctx.status(HttpStatus.OK);
            ctx.json(mappedReviewDTO, ReviewDTO.class);
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
            String idParam = ctx.queryParam("id");
            Long id = Long.valueOf(idParam);

            Set<Review> foundReviewSet = reviewDAO.getByGameId(id);
            Set<ReviewDTO> mappedReviewDTOSet = foundReviewSet.stream().map(reviewMapper::convertToDTO).collect(Collectors.toSet());

            ctx.status(HttpStatus.OK);
            ctx.json(mappedReviewDTOSet, ReviewDTO.class);
        } catch (NumberFormatException | MappingException e) {
            throw new BadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            ReviewDTO reviewDTO = ctx.bodyValidator(ReviewDTO.class).get();
            Review mappedReview = reviewMapper.convertToEntity(reviewDTO);

            Review createdReview = reviewDAO.create(mappedReview);
            ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(createdReview);

            ctx.status(HttpStatus.CREATED);
            ctx.json(mappedReviewDTO, ReviewDTO.class);
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
    public void update(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            ReviewDTO reviewDTO = ctx.bodyValidator(ReviewDTO.class).get();
            Review mappedReview = reviewMapper.convertToEntity(reviewDTO);

            Review updatedReview = reviewDAO.update(id, mappedReview);
            ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(updatedReview);

            ctx.status(HttpStatus.OK);
            ctx.json(mappedReviewDTO, ReviewDTO.class);
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

            reviewDAO.delete(id);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }
}