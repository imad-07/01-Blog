package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for authentication failures (HTTP 401).
 */
public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
