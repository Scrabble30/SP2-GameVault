package app.exception;

public class TokenValidationException extends Exception {

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
