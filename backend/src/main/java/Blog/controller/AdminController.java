package Blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.AdminPost;
import Blog.model.AdminResponse;
import Blog.model.Author;
import Blog.model.ReportResponse;
import Blog.model.UserReportResponse;
import Blog.service.AdminService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<AdminResponse> Admin() {
        AdminResponse admin = adminService.Admin();
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<List<AdminPost>> GetPost(@PathVariable("id") long id) {
        List<AdminPost> ps = adminService.getAdminPostsList(id);
        return ResponseEntity.status(HttpStatus.OK).body(ps);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> DeletePost(@PathVariable("id") long id) {
        Boolean result = adminService.DeletePost(id);
        return result ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Boolean> UpdateStatusPost(@PathVariable("id") long id) {
        Boolean result = adminService.Updatestatus(id);
        return result ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PatchMapping("/report/{id}")
    public ResponseEntity<Boolean> UpdateStatusReport(@PathVariable("id") long id) {
        Boolean result = adminService.HandleReport(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/userstatus/{id}")
    public ResponseEntity<Boolean> UpdateStatusUser(@PathVariable("id") long id) {
        Boolean result = adminService.UpdateUserStatus(id);
        return result ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<List<ReportResponse>> getReports(@PathVariable("id") long id) {
        List<ReportResponse> ps = adminService.getAdminReportList(id);
        return ResponseEntity.status(HttpStatus.OK).body(ps);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Author>> getUsers(@PathVariable("id") long id) {
        List<Author> ps = adminService.getAdminUsersList(id);
        return ResponseEntity.status(HttpStatus.OK).body(ps);
    }

    @DeleteMapping("/rm-user/{id}")
    public ResponseEntity<Boolean> DeleteUser(@PathVariable("id") long id) {
        Boolean result = adminService.DeleteUser(id);
        return result ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * Get paginated user reports for admin panel
     */
    @GetMapping("/user-reports/{id}")
    public ResponseEntity<List<UserReportResponse>> getUserReports(@PathVariable("id") long id) {
        List<UserReportResponse> reports = adminService.getAdminUserReportList(id);
        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }

    /**
     * Mark a user report as handled
     */
    @PatchMapping("/user-report/{id}")
    public ResponseEntity<Boolean> handleUserReport(@PathVariable("id") long id) {
        Boolean result = adminService.handleUserReport(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
