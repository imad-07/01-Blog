package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.service.LikeService;

@RestController
@RequestMapping("/like")
public class LikeController {
    private LikeService likeservice;

    public LikeController(LikeService likeservice) {
        this.likeservice = likeservice;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> Like(@AuthenticationPrincipal UserDetails user, @PathVariable("id") long id) {
        likeservice.Like(user, id);
        return ResponseEntity.status(HttpStatus.OK).body(java.util.Map.of("message", "Post liked successfully"));
    }
}
