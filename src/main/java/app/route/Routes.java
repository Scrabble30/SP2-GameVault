package app.route;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final GameRoutes gameRoutes;

    public Routes(EntityManagerFactory emf) {
        this.gameRoutes = new GameRoutes(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/games", gameRoutes.getGameRoutes());
        };
    }
}
