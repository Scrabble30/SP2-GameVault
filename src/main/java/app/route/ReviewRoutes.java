package app.route;

import app.controller.Controller;
import app.controller.impl.ReviewControllerImpl;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ReviewRoutes {

    private final Controller reviewControllerImpl;

    public ReviewRoutes(EntityManagerFactory emf) {
        reviewControllerImpl = ReviewControllerImpl.getInstance(emf);
    }

    protected EndpointGroup getReviewRoutes() {
        return () -> {
            get("/", reviewControllerImpl::getAll, AppRouteRole.ANYONE);
            get("/{id}", reviewControllerImpl::getById, AppRouteRole.ANYONE);
            post("/", reviewControllerImpl::create, AppRouteRole.USER);
            put("/{id}", reviewControllerImpl::update, AppRouteRole.USER);
            delete("/{id}", reviewControllerImpl::delete, AppRouteRole.USER);
        };
    }
}