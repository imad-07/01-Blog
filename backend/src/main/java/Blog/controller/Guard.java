package Blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guard")
public class Guard {
    @GetMapping()
    public boolean guard() {
        return true;
    }
}
