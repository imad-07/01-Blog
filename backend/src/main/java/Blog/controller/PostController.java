package Blog.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Blog.model.Epost;
import Blog.model.Post;
import Blog.model.PostResponse;
import Blog.service.PostService;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService service;

    public PostController(PostService ps) {
        this.service = ps;
    }

    @PostMapping
    public ResponseEntity<Object> Addpost(@AuthenticationPrincipal UserDetails user,
            @RequestParam("content") String content,
            @RequestParam("title") String title,
            @RequestParam(value = "media", required = false) MultipartFile file) {
        Post post = new Post();
        post.setContent(Jsoup.clean(content, Safelist.basic()));
        post.setTitle(Jsoup.clean(title, Safelist.basic()));

        service.AddPost(user, post, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of("message", "Post created successfully"));
    }

    @GetMapping("/{st}")
    public ResponseEntity<List<PostResponse>> GetPosts(@PathVariable long st,
            @AuthenticationPrincipal UserDetails user) {
        List<PostResponse> posts = this.service.GetPosts(st, user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> editPost(
            @AuthenticationPrincipal UserDetails user,
            @RequestPart("post") Epost bpost,
            @RequestPart(value = "media", required = false) MultipartFile media) {

        if (media != null) {
            bpost.setMedia(media);
        }
        service.EditPost(user, bpost);
        return ResponseEntity.status(HttpStatus.OK)
                .body(java.util.Map.of("message", "Post updated successfully"));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PostResponse> GetPost(@PathVariable long id) {
        PostResponse p = service.GetPost(id);
        return ResponseEntity.status(HttpStatus.OK).body(p);
    }

    @GetMapping("/mine/{st}")
    public ResponseEntity<List<PostResponse>> GetMyPosts(@PathVariable long st,
            @AuthenticationPrincipal UserDetails user) {
        List<PostResponse> ps = service.getmyPosts(st, user);
        return ResponseEntity.status(HttpStatus.OK).body(ps);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> DeletePost(@AuthenticationPrincipal UserDetails user, @PathVariable long id) {
        boolean rsp = this.service.deletemypost(user, id);
        return ResponseEntity.status(HttpStatus.OK).body(rsp);
    }
}
