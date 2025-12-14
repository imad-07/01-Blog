package Blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.CommentResponse;
import Blog.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentservice;

    public CommentController(CommentService commentservice) {
        this.commentservice = commentservice;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> Comment(@AuthenticationPrincipal UserDetails user, @PathVariable("id") long id,
            @RequestBody String content) {
        commentservice.AddComment(user, id, content);
        return ResponseEntity.status(HttpStatus.OK).body(java.util.Map.of("message", "Comment added successfully"));
    }

    @GetMapping("/{id}/{start}")
    public ResponseEntity<List<CommentResponse>> GetComment(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long id, @PathVariable("start") long start) {
        List<CommentResponse> cmt = commentservice.GetComments(id, start);
        return ResponseEntity.status(HttpStatus.OK).body(cmt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteComment(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long id) {
        commentservice.DeleteComment(user, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
