package Blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.NotificationResponse;
import Blog.service.NotificationService;

@RestController
@RequestMapping("/notif")
public class NotificationController {
    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<NotificationResponse>> Getnotifs(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long startid) {
        List<NotificationResponse> list = notificationService.Getnotification(user.getUsername(), startid);
        return ResponseEntity.status(HttpStatus.OK).body(list);

    }

    @PatchMapping("/seen/{id}")
    public ResponseEntity<Boolean> UpdateSeen(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long id) {
        Boolean result = notificationService.toggleseen(user.getUsername(), id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<Boolean> Delete(@AuthenticationPrincipal UserDetails user, @PathVariable("id") long id) {
        Boolean result = notificationService.delete(user.getUsername(), id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/delete-all")
    public ResponseEntity<Boolean> DeleteAll(@AuthenticationPrincipal UserDetails user) {
        Boolean result = notificationService.deleteAll(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/seen-all")
    public ResponseEntity<Boolean> SeenAll(@AuthenticationPrincipal UserDetails user) {
        Boolean result = notificationService.allleseen(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
