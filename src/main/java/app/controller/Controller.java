package app.controller;

import io.javalin.http.Context;

public interface Controller {
    void read(Context ctx);

    void readAll(Context ctx);

    void create(Context ctx);

    void update(Context ctx);

    void delete(Context ctx);
}
