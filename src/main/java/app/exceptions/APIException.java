package app.exceptions;

import io.javalin.http.HttpStatus;
import lombok.Getter;

@Getter
public class APIException extends RuntimeException {

    private final int statusCode;

    public APIException(int statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
    }

    public APIException(HttpStatus status, String message) {
        this(status.getCode(), message);
    }
}
