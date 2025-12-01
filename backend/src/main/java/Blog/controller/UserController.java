package Blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Blog.service.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @SuppressWarnings("unused")
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}
