package app.route;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final PlatformRoutes platformRoutes;
    private final GameRoutes gameRoutes;
    private final ReviewRoutes reviewRoutes;

    public Routes(EntityManagerFactory emf) {
        this.platformRoutes = new PlatformRoutes(emf);
        this.gameRoutes = new GameRoutes(emf);
        this.reviewRoutes = new ReviewRoutes(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/platforms", platformRoutes.getPlatformRoutes());
            path("/games", gameRoutes.getGameRoutes());
            path("/reviews", reviewRoutes.getReviewRoutes());
        };
    }
}
