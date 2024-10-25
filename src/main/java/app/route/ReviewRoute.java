package app.route;

import app.controller.Controller;
import app.controller.impl.ReviewControllerImpl;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ReviewRoute {

    private final Controller reviewControllerImpl;

    public ReviewRoute(EntityManagerFactory emf) {
        reviewControllerImpl = ReviewControllerImpl.getInstance(emf);
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            get("/{id}", reviewControllerImpl::getById, AppRouteRole.ANYONE);
            get("/", reviewControllerImpl::getAll, AppRouteRole.ANYONE);
            post("/", reviewControllerImpl::create, AppRouteRole.USER);
            put("/{id}", reviewControllerImpl::update, AppRouteRole.USER);
            delete("/{id}", reviewControllerImpl::delete, AppRouteRole.USER);
        };
    }
}