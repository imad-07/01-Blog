package Blog.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import Blog.exception.AppException;

/**
 * Global exception handler for all application exceptions.
 * Ensures consistent error response format across all endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Handles all AppException subclasses (BadRequest, NotFound, etc.)
         */
        @ExceptionHandler(AppException.class)
        public ResponseEntity<String> handleAppException(AppException ex) {
                logger.error("Application error: {} - {}", ex.getStatus(), ex.getMessage());

                String errorResponse = ex.getMessage();

                return ResponseEntity
                                .status(ex.getStatus())
                                .body(errorResponse);
        }

        /**
         * Handles Spring Security authentication exceptions (login failures)
         */
        @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
        public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
                logger.error("Authentication failed: {}", ex.getMessage());

                String errorResponse = "Invalid username or password";

                return ResponseEntity
                                .status(401)
                                .body(errorResponse);
        }

        /**
         * Handles all unexpected exceptions
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleGeneralException(Exception ex) {
                logger.error("Unexpected error occurred", ex);

                String errorResponse ="Internal server error";

                return ResponseEntity
                                .status(500)
                                .body(errorResponse);
        }
}
