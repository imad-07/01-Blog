package Blog.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Errors;
import Blog.model.Like;
import Blog.model.Post;
import Blog.model.PostResponse;
import Blog.model.User;
import Blog.repository.LikeRepository;

@Service
public class LikeService {
    private final UserService userService;
    private final PostService postService;
    private final LikeRepository likeRepository;

    public LikeService(UserService userService, PostService postService, LikeRepository likeRepository) {
        this.userService = userService;
        this.postService = postService;
        this.likeRepository = likeRepository;
    }

    public Integer CountPost(long postid) {
        return likeRepository.countByPostId(postid);
    }

    public Errors.Like_Error Like(UserDetails usr, long postid) {

        User user = userService.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            return Errors.Like_Error.InvalidUser;
        }
        if (likeRepository.existsByUserIdAndPostId(user.getId(), postid)) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), postid);
            return Errors.Like_Error.Existing;
        }
        Post post = postService.postrepository.findById(postid).orElse(null);
        if (post == null) {
            return Errors.Like_Error.InvalidPost;
        }
        Like like = new Like(user, post);
        this.likeRepository.save(like);
        return Errors.Like_Error.Success;
    }

    public PostResponse getMostLikedPost(long userId) {
        long id = likeRepository.findMostLikedPostId();
        Post p = postService.postrepository.findById(id).orElse(null);
        boolean isliked = likeRepository.existsByUserIdAndPostId(userId, p.getId());
        PostResponse pr = postService.toPostResponse(p, isliked);
        return pr;
    }
}
 