package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for authorization failures (HTTP 403).
 */
public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
