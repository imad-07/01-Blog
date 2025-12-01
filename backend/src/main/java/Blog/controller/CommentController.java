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
import Blog.model.Errors;
import Blog.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentservice;

    public CommentController(CommentService commentservice) {
        this.commentservice = commentservice;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> Comment(@AuthenticationPrincipal UserDetails user, @PathVariable("id") long id,
            @RequestBody String content) {
        Errors.Comment_Error e = commentservice.AddComment(user, id, content);
        switch (e) {
            case Errors.Comment_Error.InvalidLength:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("InvalidLength");
            case Errors.Comment_Error.InvalidPost:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("InvalidPost");
            case Errors.Comment_Error.InvalidUser:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("InvalidUser");
            default:
                return ResponseEntity.status(HttpStatus.OK).body("Comment Added Successfully");
        }
    }

    @GetMapping("/{id}/{start}")
    public ResponseEntity<List<CommentResponse>> GetComment(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long id, @PathVariable("start") long start) {
        List<CommentResponse> cmt = commentservice.GetComments(id, start);
        return ResponseEntity.status(HttpStatus.OK).body(cmt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> DeleteComment(@AuthenticationPrincipal UserDetails user,
            @PathVariable("id") long id) {
        String rsp = commentservice.DeleteComment(user, id);
        return ResponseEntity.status(HttpStatus.OK).body(rsp);
    }
}
