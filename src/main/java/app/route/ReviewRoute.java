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
        reviewControllerImpl = new ReviewControllerImpl(emf);
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/", reviewControllerImpl::create, AppRouteRole.USER);
            get("/{id}", reviewControllerImpl::read, AppRouteRole.ANYONE);
            put("/{id}", reviewControllerImpl::update, AppRouteRole.USER);
            delete("/{id}", reviewControllerImpl::delete, AppRouteRole.USER);
        };
    }
}
