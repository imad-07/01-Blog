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
import Blog.model.Errors;
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
    public ResponseEntity<String> Addpost(@AuthenticationPrincipal UserDetails user,
            @RequestParam("content") String content,
            @RequestParam("title") String title,
            @RequestParam(value = "media", required = false) MultipartFile file) {
        Post post = new Post();

        post.setContent(Jsoup.clean(content, Safelist.basic()));
        post.setTitle(Jsoup.clean(title, Safelist.basic()));
        if (user == null) {
            System.exit(0);
        }
        Errors.Post_Error err = service.AddPost(user, post, file);
        switch (err) {
            case Errors.Post_Error.InvalidLength:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Content Length");
            case Errors.Post_Error.InvalidTitle:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Title Length");
            case Errors.Post_Error.Media:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Media Size");
            case Errors.Post_Error.IO:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            default:
                return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        }
    }

    @GetMapping("/{st}")
    public ResponseEntity<List<PostResponse>> GetPosts(@PathVariable long st,
            @AuthenticationPrincipal UserDetails user) {
        List<PostResponse> posts = this.service.GetPosts(st, user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editPost(
            @AuthenticationPrincipal UserDetails user,
            @RequestPart("post") Epost bpost,
            @RequestPart(value = "media", required = false) MultipartFile media

    ) {
        if (media != null) {
            bpost.setMedia(media);
        }
        Errors.Post_Error Err = service.EditPost(user, bpost);
        switch (Err) {
            case Errors.Post_Error.InvalidLength:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Content Length");
            case Errors.Post_Error.InvalidTitle:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Title Length");
            case Errors.Post_Error.Media:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Media Size");
            case Errors.Post_Error.Fraud:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You only edit you own posts");
            case Errors.Post_Error.Internal:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            default:
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Success");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PostResponse> GetPost(@PathVariable long id) {

        PostResponse p = service.GetPost(id);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
