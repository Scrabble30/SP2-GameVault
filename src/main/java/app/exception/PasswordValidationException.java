package app.exception;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordValidationException(String message) {
        super(message);
    }
}
