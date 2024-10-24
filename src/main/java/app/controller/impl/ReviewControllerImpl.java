package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.ReviewDAOImpl;
import app.dto.ReviewDTO;
import app.entity.Review;
import app.mapper.ReviewMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ReviewControllerImpl implements Controller {
    private final Logger logger = LoggerFactory.getLogger(ReviewControllerImpl.class);

    private final ReviewDAOImpl reviewDAO;
    private final ReviewMapper reviewMapper = new ReviewMapper();

    public ReviewControllerImpl(EntityManagerFactory emf) {
        this.reviewDAO = ReviewDAOImpl.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        Review foundReview = reviewDAO.getById(id);
        ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(foundReview);

        ctx.res().setStatus(200);
        ctx.json(mappedReviewDTO, ReviewDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        Set<Review> foundReviewSet = reviewDAO.getByGameId(id);
        Set<ReviewDTO> mappedReviewDTOSet = foundReviewSet.stream().map(reviewMapper::convertToDTO).collect(Collectors.toSet());

        ctx.res().setStatus(200);
        ctx.json(mappedReviewDTOSet, ReviewDTO.class);
    }

    @Override
    public void create(Context ctx) {
        ReviewDTO reviewDTO = ctx.bodyAsClass(ReviewDTO.class);
        Review mappedReview = reviewMapper.convertToEntity(reviewDTO);

        Review createdReview = reviewDAO.create(mappedReview);
        ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(createdReview);

        ctx.res().setStatus(201);
        ctx.json(mappedReviewDTO, ReviewDTO.class);
    }

    @Override
    public void update(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        ReviewDTO reviewDTO = ctx.bodyAsClass(ReviewDTO.class);
        Review mappedReview = reviewMapper.convertToEntity(reviewDTO);

        Review updatedReview = reviewDAO.update(id, mappedReview);
        ReviewDTO mappedReviewDTO = reviewMapper.convertToDTO(updatedReview);

        ctx.res().setStatus(200);
        ctx.json(mappedReviewDTO, ReviewDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        reviewDAO.delete(id);

        ctx.res().setStatus(204);
    }
}
