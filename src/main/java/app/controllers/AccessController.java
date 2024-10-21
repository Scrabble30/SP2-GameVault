package app.controllers;

import app.enums.AppRouteRole;
import app.exceptions.APIException;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class AccessController {

    private final SecurityController securityController;

    public AccessController(EntityManagerFactory emf) {
        this.securityController = SecurityController.getInstance(emf);
    }

    public void handleAccess(Context ctx) {
        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(AppRouteRole.ANYONE)) {
            return;
        }

        if (ctx.method().equals(HandlerType.OPTIONS)) {
            return;
        }

        UserDTO userDTO = securityController.authenticate(ctx);
        Set<String> userRoles = userDTO.getRoles().stream().map(String::toUpperCase).collect(Collectors.toSet());
        Set<String> allowedRoles = ctx.routeRoles().stream().map(RouteRole::toString).collect(Collectors.toSet());

        if (allowedRoles.stream().noneMatch(userRoles::contains)) {
            throw new APIException(HttpStatus.FORBIDDEN, String.format("Unauthorized with user roles: %s. Needed Roles: %s", userRoles, allowedRoles));
        }
    }
}
