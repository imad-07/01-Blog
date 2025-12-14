package Blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for validation errors and invalid input (HTTP 400).
 */
public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
