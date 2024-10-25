package app.route;

import app.controller.impl.GameControllerImpl;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameRoutes {

    private final GameControllerImpl gameController;

    public GameRoutes(EntityManagerFactory emf) {
        this.gameController = GameControllerImpl.getInstance(emf);
    }

    public EndpointGroup getGameRoutes() {
        return () -> {
            get("/", gameController::getAll, AppRouteRole.ANYONE);
            get("/{id}", gameController::getById, AppRouteRole.ANYONE);
            get("/{id}/reviews", gameController::getAllReviews, AppRouteRole.ANYONE);
            post("/", gameController::create, AppRouteRole.ADMIN);
            put("/{id}", gameController::update, AppRouteRole.ADMIN);
            delete("/{id}", gameController::delete, AppRouteRole.ADMIN);
        };
    }
}
