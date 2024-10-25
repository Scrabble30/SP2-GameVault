package app.route;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final GameRoutes gameRoutes;
    private final ReviewRoute reviewRoute;

    public Routes(EntityManagerFactory emf) {
        this.gameRoutes = new GameRoutes(emf);
        this.reviewRoute = new ReviewRoute(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/games", gameRoutes.getGameRoutes());
            path("/reviews", reviewRoute.getRoutes());
        };
    }
}
