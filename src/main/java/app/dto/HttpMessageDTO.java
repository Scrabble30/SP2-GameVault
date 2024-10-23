package app.dto;

import io.javalin.http.HttpStatus;
import lombok.Getter;

@Getter
public class HttpMessageDTO {

    private final int status;
    private final String message;

    public HttpMessageDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpMessageDTO(HttpStatus status, String message) {
        this(status.getCode(), message);
    }
}
