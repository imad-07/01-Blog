package Blog.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Like;
import Blog.model.NotificationType;
import Blog.model.Post;
import Blog.model.PostResponse;
import Blog.model.User;
import Blog.repository.LikeRepository;

@Service
public class LikeService {
    private final UserService userService;
    private final PostService postService;
    private final LikeRepository likeRepository;
    private NotificationService notifservice;

    public LikeService(UserService userService, PostService postService, LikeRepository likeRepository,
            NotificationService notifservice) {
        this.userService = userService;
        this.postService = postService;
        this.likeRepository = likeRepository;
        this.notifservice = notifservice;
    }

    public Integer CountPost(long postid) {
        return likeRepository.countByPostId(postid);
    }

    public void Like(UserDetails usr, long postid) {
        User user = userService.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            throw new Blog.exception.NotFoundException("User not found");
        }

        // Toggle like: if exists, remove it; otherwise add it
        if (likeRepository.existsByUserIdAndPostId(user.getId(), postid)) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), postid);
            return;
        }

        Post post = postService.postrepository.findById(postid).orElse(null);
        if (post == null || !post.isStatus()) {
            throw new Blog.exception.NotFoundException("Post not found");
        }

        Like like = new Like(user, post);
        this.likeRepository.save(like);
        notifservice.save(user, post.getAuthor(), NotificationType.LIKE);
    }

    public PostResponse getMostLikedPost(long userId) {
        Long id = likeRepository.findMostLikedPostId();
        if (id == null) {
            return null;
        }
        Post p = postService.postrepository.findById(id).orElse(null);
        if (p == null) {
            return null;
        }
        boolean isliked = likeRepository.existsByUserIdAndPostId(userId, p.getId());
        PostResponse pr = postService.toPostResponse(p, isliked);
        return pr;
    }
}
