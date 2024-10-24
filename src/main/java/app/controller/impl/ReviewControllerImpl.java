package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.ReviewDAOImpl;
import app.dto.ReviewDTO;
import app.entity.Review;
import app.mapper.ReviewMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class ReviewControllerImpl implements Controller {
    private final ReviewDAOImpl reviewDAO;
    private final ReviewMapper reviewMapper = new ReviewMapper();

    public ReviewControllerImpl(EntityManagerFactory emf) {
        this.reviewDAO = ReviewDAOImpl.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        Review review = reviewDAO.getById(id);
        ReviewDTO reviewDTO = reviewMapper.convertToDTO(review);

        ctx.res().setStatus(200);
        ctx.json(reviewDTO, ReviewDTO.class);
    }

    @Override
    public void readAll(Context ctx) {

    }

    @Override
    public void create(Context ctx) {

    }

    @Override
    public void update(Context ctx) {

    }

    @Override
    public void delete(Context ctx) {

    }
}
