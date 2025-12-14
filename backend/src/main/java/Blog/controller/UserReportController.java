package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Blog.service.ReportService;

/**
 * Controller for user reporting functionality.
 */
@RestController
@RequestMapping("/user-report")
public class UserReportController {
    private final ReportService reportService;

    public UserReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Report a user for inappropriate behavior
     * 
     * @param user   The authenticated user making the report
     * @param userId The ID of the user being reported
     * @param reason The reason for the report (SPAM, HARASSMENT, etc.)
     * @return Success message
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Object> reportUser(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable("userId") long userId,
            @RequestParam String reason) {
        reportService.reportUser(user, userId, reason);
        return ResponseEntity.status(HttpStatus.OK).body(java.util.Map.of("message", "User reported successfully"));
    }
}
