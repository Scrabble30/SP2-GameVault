package app.route;

import app.controller.SecurityController;
import app.dto.HttpMessageDTO;
import app.enums.AppRouteRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {

    private final SecurityController securityController;

    public SecurityRoutes(EntityManagerFactory emf) {
        this.securityController = SecurityController.getInstance(emf);
    }

    public EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                get("/test", ctx -> ctx.json(new HttpMessageDTO(200, "Hello from open."), HttpMessageDTO.class), AppRouteRole.ANYONE);
                post("/login", securityController::login, AppRouteRole.ANYONE);
                post("/register", securityController::register, AppRouteRole.ANYONE);
            });
        };
    }

    public EndpointGroup getProtectedDemoRoutes() {
        return () -> {
            path("/protected", () -> {
                get("/user_demo", ctx -> ctx.json(new HttpMessageDTO(200, "Hello from user protected."), HttpMessageDTO.class), AppRouteRole.USER);
                get("/admin_demo", ctx -> ctx.json(new HttpMessageDTO(200, "Hello from admin protected."), HttpMessageDTO.class), AppRouteRole.ADMIN);
            });
        };
    }
}
