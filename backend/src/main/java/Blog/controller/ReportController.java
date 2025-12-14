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

@RestController
@RequestMapping("/repport")
public class ReportController {
    private final ReportService reportservice;

    public ReportController(ReportService reportservice) {
        this.reportservice = reportservice;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> Report(@AuthenticationPrincipal UserDetails user, @RequestParam String reason,
            @PathVariable("id") long id) {
        reportservice.Report(user, id, reason);
        return ResponseEntity.status(HttpStatus.OK).body(java.util.Map.of("message", "Post reported successfully"));
    }

}
