package app.enums;

import io.javalin.security.RouteRole;

public enum AppRouteRole implements RouteRole {
    ANYONE,
    USER,
    ADMIN
}
