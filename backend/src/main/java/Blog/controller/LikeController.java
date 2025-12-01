package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.Errors;
import Blog.service.LikeService;

@RestController
@RequestMapping("/like")
public class LikeController {
    private LikeService likeservice;
    public LikeController(LikeService likeservice){
        this.likeservice = likeservice;
    }
    @PostMapping("/{id}")
    public ResponseEntity<String> Like(@AuthenticationPrincipal UserDetails user, @PathVariable("id") long id) {
        Errors.Like_Error err = likeservice.Like(user, id);
        switch (err) {
            case Errors.Like_Error.InvalidPost:
                return ResponseEntity.status(HttpStatus.OK).body("InvalidPostid");
            case Errors.Like_Error.InvalidUser:
                return ResponseEntity.status(HttpStatus.OK).body("InvalidUser"); 
            case Errors.Like_Error.Existing:
                return ResponseEntity.status(HttpStatus.OK).body("AlreadyLiked"); 
            default:
               return ResponseEntity.status(HttpStatus.OK).body("PostLikedSuccess");  
        }
    }
}
