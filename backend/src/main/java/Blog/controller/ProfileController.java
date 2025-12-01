package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.ProfileResponse;
import Blog.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private ProfileService ps;

    public ProfileController(ProfileService ps) {
        this.ps = ps;
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> Profile(@AuthenticationPrincipal UserDetails user,  @PathVariable("username") String username){
        ProfileResponse pr = ps.getProfile(user,username);
        return ResponseEntity.status(HttpStatus.OK).body(pr);
    }
}
