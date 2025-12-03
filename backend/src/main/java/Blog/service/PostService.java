package Blog.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import Blog.helpers.validators;
import Blog.model.Author;
import Blog.model.Epost;
import Blog.model.Errors;
import Blog.model.Post;
import Blog.model.PostResponse;
import Blog.model.PostResponse.Media;
import Blog.model.User;
import Blog.repository.LikeRepository;
import Blog.repository.PostRepository;

@Service
public class PostService {
    public final PostRepository postrepository;
    public final LikeRepository likerepository;
    public final CommentService commentService;
    private final UserService uservice;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private final String myDir = "src/main/java/Blog/files/posts/";

    @Autowired
    public PostService(PostRepository PostRepository, UserService uservice, LikeRepository likerepository,
            CommentService commentService) {
        this.postrepository = PostRepository;
        this.likerepository = likerepository;
        this.commentService = commentService;
        this.uservice = uservice;
    }

    public Errors.Post_Error AddPost(UserDetails user, Post post, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                return Errors.Post_Error.Media;
            }
            try {
                Files.createDirectories(Paths.get(myDir));
                String fileName = UUID.randomUUID() + "." + file.getOriginalFilename();
                Path filePath = Paths.get(myDir).resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                post.setMedia(myDir + fileName);
            } catch (IOException e) {
                return Errors.Post_Error.IO;
            }
        }
        if (!validators.ValidateContent(post.getContent())) {
            return Errors.Post_Error.InvalidLength;
        } else if (!validators.Validatetitle(post.getTitle())) {
            return Errors.Post_Error.InvalidTitle;
        }
        post.setAuthor(uservice.getUserByUsername(user.getUsername()).orElse(null));
        post.setStatus(true);
        postrepository.save(post);
        return Errors.Post_Error.Success;
    }

    public List<PostResponse> GetPosts(long start, String usr) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Post p = postrepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        User user = uservice.getUserByUsername(usr).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Post> posts = postrepository.findFeedPage(user.getId(),start);

        List<PostResponse> respons = toPostResponseList(posts, user.getId());
        return respons;
    }

    public Errors.Post_Error EditPost(UserDetails user, Epost bpost) {

        if (!postrepository.getAuthId(bpost.getId()).equals(user.getUsername()) && !user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return Errors.Post_Error.Fraud;
        }
        if (bpost.getContent() != null) {
            if (!validators.ValidateContent(bpost.getContent())) {
                return Errors.Post_Error.InvalidLength;
            }

            if (postrepository.updateContentById(bpost.getId(), bpost.getContent()) != 1) {
                return Errors.Post_Error.Internal;
            }
        }
        if (bpost.getTitle() != null) {
            if (!validators.Validatetitle(bpost.getTitle())) {
                return Errors.Post_Error.InvalidTitle;
            }
            if (postrepository.updateTitleById(bpost.getId(), bpost.getTitle()) != 1) {
                return Errors.Post_Error.Internal;
            }
        }
        if (bpost.getMedia() != null && !bpost.getMedia().isEmpty()) {
            if (bpost.getMedia().getSize() > MAX_FILE_SIZE) {
                return Errors.Post_Error.Media;
            }
            String m = postrepository.getMedia(bpost.getId());
            String newtype = validators.getFileType(bpost.getMedia().getOriginalFilename());
            String extype = validators.getFileType(m);
            boolean x = newtype.equals(extype);
            String fileName = "";
            try {
                Files.createDirectories(Paths.get(myDir));
                fileName = (m.isEmpty() || !x)
                        ? UUID.randomUUID() + "." + bpost.getMedia().getOriginalFilename()
                        : m.replaceFirst(myDir, "");
                Path filePath = Paths.get(myDir).resolve(fileName);
                Files.copy(bpost.getMedia().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return Errors.Post_Error.IO;
            }
            if (!x) {
                postrepository.updateMediaById(bpost.getId(), fileName);
            }
        }
        return Errors.Post_Error.Success;
    }

    public PostResponse GetPost(long id) {
        Post p = postrepository.findById(id).orElse(null);
        if (p == null) {
            return null;
        }
        User u = uservice.getUserByUsername(p.getAuthor().getUsername()).orElse(null);
        if (u == null) {
            return null;
        }
        boolean isliked = likerepository.existsByUserIdAndPostId(u.getId(), id);
        PostResponse pr = toPostResponse(p, isliked);
        return pr;
    }

    public List<PostResponse> GetUserPosts(long start, User usr) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Post p = postrepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        List<Post> posts = postrepository.findTop20ByIdLessThanAndAuthorOrderByIdDesc(start, usr);
        List<PostResponse> respons = toPostResponseList(posts, usr.getId());
        return respons;
    }

    public List<PostResponse> GetActiveUserPosts(long start, User usr) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Post p = postrepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        List<Post> posts = postrepository.findTop20ByIdLessThanAndAuthorOrderByIdDesc(start, usr);
        List<PostResponse> respons = toPostResponseList(posts, usr.getId());
        return respons;
    }

    public List<PostResponse> getmyPosts(long start, UserDetails usr) {
        User user = uservice.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            return null;
        }
        List<PostResponse> posts = GetUserPosts(start, user);
        return posts;
    }

    public List<PostResponse> toPostResponseList(List<Post> posts, long userId) {
        List<PostResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            boolean isliked = likerepository.existsByUserIdAndPostId(userId, post.getId());
            PostResponse response = toPostResponse(post, isliked);
            if (response == null) {
                return new ArrayList<>();
            }
            responses.add(response);
        }

        return responses;
    }

    public PostResponse toPostResponse(Post post, boolean isliked) {
        PostResponse response = new PostResponse();

        response.setLikes(likerepository.countByPostId(post.getId()));
        response.setLiked(isliked);

        User user = uservice.getUserByUsername(post.getAuthor().getUsername()).orElse(null);
        if (user == null)
            return null;
        response.setAuthor(new Author(user.getId(), user.getAvatar(), user.getUsername(), user.isStatus()));

        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setTimestamp(Date.from(post.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));

        String mediaPath = post.getMedia();
        if (mediaPath != null && !mediaPath.isEmpty()) {
            File file = new File(myDir + mediaPath);
            if (file.exists()) {
                try {
                    String type = Files.probeContentType(file.toPath());
                    Media media = new Media();
                    media.setUrl(mediaPath.replaceFirst("^" + "src/main/files/posts/", ""));
                    if (type.startsWith("image"))
                        media.setType("image");
                    else if (type.startsWith("video"))
                        media.setType("video");
                    response.setMedia(media);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        response.setCmts(commentService.Getcommentcount(post.getId()));
        response.setComments(commentService.GetComments(post.getId(), 0));

        return response;
    }

    public List<Post> GetAdminPosts(long start) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Post p = postrepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        return postrepository.findTop20ByIdLessThanOrderByIdDesc(start);
    }

    public Boolean DeletePost(long postid) {
        return postrepository.deletePostById(postid) == 1;
    }

    public boolean Changestatus(long postid) {
        return postrepository.updatestatusById(postid) == 1;
    }

    public long countPosts() {
        return postrepository.count();
    }

    public long countActivePosts() {
        return postrepository.countByStatusTrue();
    }

    public long countSuspendedPosts() {
        return postrepository.countByStatusFalse();
    }

    public boolean deletemypost(UserDetails user, long postid) {
        if (!postrepository.getAuthId(postid).equals(user.getUsername())) {
            return false;
        }
        return DeletePost(postid);
    }
}
