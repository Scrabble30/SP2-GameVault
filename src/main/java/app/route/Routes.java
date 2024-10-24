package app.route;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final ReviewRoute reviewRoute;

    public Routes(EntityManagerFactory emf) {
        reviewRoute = new ReviewRoute(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/reviews", reviewRoute.getRoutes());
        };
    }
}
