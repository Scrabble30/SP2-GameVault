package app.route;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final GameRoutes gameRoutes;
    private final ReviewRoutes reviewRoutes;

    public Routes(EntityManagerFactory emf) {
        this.gameRoutes = new GameRoutes(emf);
        this.reviewRoutes = new ReviewRoutes(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/games", gameRoutes.getGameRoutes());
            path("/reviews", reviewRoutes.getReviewRoutes());
        };
    }
}
