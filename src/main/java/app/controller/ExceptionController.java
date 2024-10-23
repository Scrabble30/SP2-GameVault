package app.controller;

import app.dto.HttpMessageDTO;
import app.exception.APIException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    public void handleAPIExceptions(APIException e, Context ctx) {
        logger.error("{} - {}", HttpStatus.forStatus(e.getStatusCode()), e.getMessage());

        ctx.status(e.getStatusCode());
        ctx.json(new HttpMessageDTO(e.getStatusCode(), e.getMessage()));
    }

    public void handleExceptions(Exception e, Context ctx) {
        logger.error("Unhandled exception: {} - {}", e.getClass().getSimpleName(), e.getMessage());

        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        ctx.json(new HttpMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
