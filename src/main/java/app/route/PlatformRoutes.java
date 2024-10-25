package app.route;

import app.controller.impl.PlatformControllerImpl;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PlatformRoutes {

    private final PlatformControllerImpl platformController;

    public PlatformRoutes(EntityManagerFactory emf) {
        this.platformController = PlatformControllerImpl.getInstance(emf);
    }

    public EndpointGroup getPlatformRoutes() {
        return () -> {
            get("/", platformController::getAll, AppRouteRole.ANYONE);
            get("/{id}", platformController::getById, AppRouteRole.ANYONE);
            post("/", platformController::create, AppRouteRole.ADMIN);
            put("/{id}", platformController::update, AppRouteRole.ADMIN);
            delete("/{id}", platformController::delete, AppRouteRole.ADMIN);
        };
    }
}
