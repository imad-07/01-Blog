package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for missing resources (HTTP 404).
 */
public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
