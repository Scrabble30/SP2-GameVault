package app.config;

import app.controllers.AccessController;
import app.controllers.ExceptionController;
import app.exceptions.APIException;
import app.routes.Routes;
import app.routes.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import jakarta.persistence.EntityManagerFactory;

public class AppConfig {

    private static ExceptionController exceptionController;
    private static AccessController accessController;
    private static SecurityRoutes securityRoutes;
    private static Routes routes;

    private static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api/v1";
        config.http.defaultContentType = "application/json";

        config.bundledPlugins.enableRouteOverview("/routes");
        config.bundledPlugins.enableDevLogging();

        config.router.apiBuilder(securityRoutes.getSecurityRoutes());
        config.router.apiBuilder(securityRoutes.getProtectedDemoRoutes());
        config.router.apiBuilder(routes.getAPIRoutes());
    }

    public static void handleExceptions(Javalin app) {
        app.exception(APIException.class, exceptionController::handleAPIExceptions);
        app.exception(Exception.class, exceptionController::handleExceptions);
    }

    public static void handleAccess(Javalin app) {
        app.beforeMatched(accessController::handleAccess);
    }

    public static Javalin startServer(int port, EntityManagerFactory emf) {
        AppConfig.exceptionController = new ExceptionController();
        AppConfig.accessController = new AccessController(emf);

        AppConfig.securityRoutes = new SecurityRoutes(emf);
        AppConfig.routes = new Routes();

        Javalin app = Javalin.create(AppConfig::configuration);
        handleExceptions(app);
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
