package app.config;

import app.controller.AccessController;
import app.controller.ExceptionController;
import app.enums.AppRouteRole;
import app.route.Routes;
import app.route.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class AppConfig {

    private static ExceptionController exceptionController;
    private static AccessController accessController;
    private static SecurityRoutes securityRoutes;
    private static Routes routes;

    private static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api";
        config.http.defaultContentType = "application/json";

        config.bundledPlugins.enableRouteOverview("/routes", AppRouteRole.ANYONE);
        config.bundledPlugins.enableDevLogging();

        config.router.apiBuilder(securityRoutes.getSecurityRoutes());
        config.router.apiBuilder(securityRoutes.getProtectedDemoRoutes());
        config.router.apiBuilder(routes.getAPIRoutes());
    }

    public static void handleExceptions(Javalin app) {
        app.exception(HttpResponseException.class, exceptionController::handleHttpResponseExceptions);
        app.exception(Exception.class, exceptionController::handleExceptions);
    }

    public static void handleAccess(Javalin app) {
        app.beforeMatched(accessController::handleAccess);
    }

    public static void handleCORS(Javalin app) {
        app.before(AppConfig::corsHeaders);
        app.options("/*", ctx -> {
            corsHeaders(ctx);
            ctx.status(HttpStatus.NO_CONTENT);
        });
    }

    private static void corsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    public static Javalin startServer(int port, EntityManagerFactory emf) {
        AppConfig.exceptionController = new ExceptionController();
        AppConfig.accessController = new AccessController(emf);

        AppConfig.securityRoutes = new SecurityRoutes(emf);
        AppConfig.routes = new Routes(emf);

        Javalin app = Javalin.create(AppConfig::configuration);
        handleExceptions(app);
        handleAccess(app);
        handleCORS(app);
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
