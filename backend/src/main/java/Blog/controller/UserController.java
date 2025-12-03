package Blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.model.Author;
import Blog.service.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Author>> GetUsers(@PathVariable("id") long id, @AuthenticationPrincipal UserDetails usr) {
        List<Author> ps = userService.getUsers(id, usr.getUsername());
        System.out.println(ps.size());
        return ResponseEntity.status(HttpStatus.OK).body(ps);
    }

}
