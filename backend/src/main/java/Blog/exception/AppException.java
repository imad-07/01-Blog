package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Base application exception that all domain-specific exceptions extend.
 * Carries HTTP status code and client-facing error message.
 */
public class AppException extends RuntimeException {
    private final HttpStatus status;

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
