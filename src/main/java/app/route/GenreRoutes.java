package app.route;

import app.controller.impl.GenreControllerImpl;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GenreRoutes {

    private final GenreControllerImpl genreController;

    public GenreRoutes(EntityManagerFactory emf) {
        this.genreController = GenreControllerImpl.getInstance(emf);
    }

    protected EndpointGroup getGenreRoutes() {
        return () -> {
            get("/", genreController::getAll, AppRouteRole.ANYONE);
            get("/{id}", genreController::getById, AppRouteRole.ANYONE);
            post("/", genreController::create, AppRouteRole.ADMIN);
            put("/{id}", genreController::update, AppRouteRole.ADMIN);
            delete("/{id}", genreController::delete, AppRouteRole.ADMIN);
        };
    }
}