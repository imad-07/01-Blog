package Blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.Author;

import Blog.service.WhoamiService;

@RestController
@RequestMapping("/whoami")
public class Whoami {
    private final WhoamiService whoamiservice;

    public Whoami(WhoamiService whoamiservice) {
        this.whoamiservice = whoamiservice;
    }

    @GetMapping
    ResponseEntity<Author> whoami(@AuthenticationPrincipal UserDetails user) {
        Author a = whoamiservice.Whoami(user);
        return ResponseEntity.status(HttpStatus.OK).body(a);
    }
}
