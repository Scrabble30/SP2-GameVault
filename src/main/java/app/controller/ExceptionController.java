package app.controller;

import app.dto.HttpMessageDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    public void handleHttpResponseExceptions(HttpResponseException e, Context ctx) {
        if (!e.getDetails().isEmpty()) {
            logger.error("{} - {} {}", HttpStatus.forStatus(e.getStatus()), e.getMessage(), e.getDetails());
        } else {
            logger.error("{} - {}", HttpStatus.forStatus(e.getStatus()), e.getMessage());
        }

        ctx.status(e.getStatus());
        ctx.json(new HttpMessageDTO(e.getStatus(), e.getMessage()));
    }

    public void handleExceptions(Exception e, Context ctx) {
        logger.error("Unhandled exception: {} - {}", e.getClass().getSimpleName(), e.getMessage());

        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        ctx.json(new HttpMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
