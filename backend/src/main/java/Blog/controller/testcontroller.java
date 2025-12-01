package Blog.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.io.IOException;

@RestController
public class testcontroller {
    @GetMapping("/test-image")
public ResponseEntity<Object> testImage() throws IOException {
     Resource file = (Resource) new ClassPathResource("files/posts/1f31a426-54a7-489f-8515-38af0260add9.twintails.jpg");
    return ResponseEntity.ok()
             .header(HttpHeaders.CONTENT_TYPE, "image/png")
            .body(file);
}
}
