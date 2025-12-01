package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Author;
import Blog.model.PostResponse;
import Blog.model.Profile;
import Blog.model.ProfileResponse;
import Blog.model.User;

@Service
public class ProfileService {
    private UserService userService;
    private PostService postService;
    private WhoamiService whoamiService;
    private FollowService followService;

    public ProfileService(UserService userService, PostService postService, WhoamiService whoamiService,
            FollowService followService) {
        this.userService = userService;
        this.postService = postService;
        this.whoamiService = whoamiService;
        this.followService = followService;
    }

    public ProfileResponse getProfile(UserDetails usr, String username) {
        Profile p = new Profile();
        List<String> errors = new ArrayList<>();
        User user = userService.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            errors.add("InvalidInfo");

            return new ProfileResponse(p, errors);
        }
        if (user.getUsername() != username) {
            user = userService.getUserByUsername(username).orElse(null);
        }
        if (user == null) {
            errors.add("InvalidInfo");
            return new ProfileResponse(p, errors);
        }
        List<PostResponse> posts = postService.GetUserPosts(0, user);
        if (posts == null) {
            String perror = "Posts";
            errors.add(perror);
        }
        p.setPosts(posts);
        Author a = whoamiService.Whoami(user);
        if (a == null) {
            String uerror = "Me";
            errors.add(uerror);
        }
        p.setuser(a);

        boolean isfollowed = followService.isFollowing(userService.getUserByUsername(usr.getUsername()).orElse(null),
                user);
        p.setFollowed(isfollowed);
        List<Author> as = followService.getFollowers(user, 0);
        if (as == null) {
            String ferror = "Followers";
            errors.add(ferror);
        }
        p.setFollowers(as);
        List<Author> fs = followService.getFollowing(user, 0);
        if (fs == null) {
            String fgerror = "Following";
            errors.add(fgerror);
        }
        p.setFollowing(fs);
        ProfileResponse pr = new ProfileResponse(p, errors);
        return pr;
    }
}
