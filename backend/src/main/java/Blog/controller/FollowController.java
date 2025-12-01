package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.service.FollowService;

@RestController
@RequestMapping("/follow")
public class FollowController {
    private FollowService followservice;
    FollowController(FollowService followservice){
        this.followservice = followservice;
    }
    @PostMapping("/{username}")
    ResponseEntity<Boolean> follow(@AuthenticationPrincipal UserDetails user, @PathVariable("username") String username){
        boolean r = followservice.follow(user, username);
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }
    
}
