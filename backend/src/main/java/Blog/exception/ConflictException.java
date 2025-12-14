package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for duplicate resources and conflicting states (HTTP 409).
 */
public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
