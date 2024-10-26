package app.controller.impl;

import app.controller.Controller;
import app.dao.impl.GenreDAOImpl;
import app.mapper.GenreMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

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

    }

    @Override
    public void getById(Context ctx) {

    }

    @Override
    public void getAll(Context ctx) {

    }

    @Override
    public void update(Context ctx) {

    }

    @Override
    public void delete(Context ctx) {

    }
}