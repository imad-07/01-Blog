package Blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Blog.helpers.JwtUtil;
import Blog.model.Responses.UserResponse;
import Blog.model.User;
import Blog.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    @Autowired
    private AuthenticationManager authman;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> Registeruser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody User userRequest) {
        authman.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(),
                        userRequest.getPassword()));

        User user = userService.getUserByUsername(userRequest.getUsername()).orElse(null);
        String jwt = JwtUtil.generateToken(user);
        return ResponseEntity.ok(
                new UserResponse("Login Succeeded", user.getId() + "", user.getUsername(), jwt));
    }

}
