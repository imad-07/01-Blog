package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Author;
import Blog.model.Follow;
import Blog.model.User;
import Blog.repository.FollowRepository;

@Service
public class FollowService {
    private FollowRepository followRepository;
    private UserService userService;

    FollowService(FollowRepository followRepository, UserService userService) {
        this.followRepository = followRepository;
        this.userService = userService;
    }

    public List<Author> getFollowers(User user, long startId) {
        if (startId == 0) {
            Follow follow = followRepository.findTop1ByFollowedOrderByIdDesc(user);
            if (follow == null) {
                return new ArrayList<>();
            }
            long latestFollower = follow.getId();
            startId = latestFollower + 1;

        }
        List<Follow> us = followRepository.findTop100ByFollowedAndIdLessThanOrderByIdDesc(user,
                startId);
        return toAuthorList(us, true);
    }

    public List<Author> getFollowing(User user, long startId) {
        if (startId == 0) {
            Follow follow = followRepository.findTop1ByFollowerOrderByIdDesc(user);
            if (follow == null) {
                return new ArrayList<>();
            }
            long latestFollowed = follow.getId();
            startId = latestFollowed + 1;
        }
        List<Follow> us = followRepository.findTop100ByFollowerAndIdLessThanOrderByIdDesc(user, startId);
        return toAuthorList(us, false);
    }

    public boolean isFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }

    public void unfollow(User follower, User followed) {
        followRepository.deleteByFollowerAndFollowed(follower, followed);
    }

    public boolean follow(UserDetails flwer, String flwed) {
        User follower = userService.getUserByUsername(flwer.getUsername()).orElse(null);
        User followed = userService.getUserByUsername(flwed).orElse(null);
        if (followed == null || follower == null || followed.getUsername().equals(follower.getUsername())) {
            return false;
        }
        if (isFollowing(follower, followed)) {
            unfollow(follower, followed);
        } else {
            Follow f = new Follow();
            f.setFollower(follower);
            f.setFollowed(followed);
            followRepository.save(f);
        }
        return true;
    }

    public Author toAuthor(long userid) {
        User user = userService.getUserById(userid).orElse(null);
        if (user == null)
            return null;
        return new Author(user.getId(),user.getAvatar(), user.getUsername(), user.isStatus());
    }

    public List<Author> toAuthorList(List<Follow> follows, boolean followers) {
        if (follows == null || follows.contains(null)) {
            return null;
        }

        List<Author> authors = new ArrayList<>();
        for (Follow follow : follows) {
            User user = followers ? follow.getFollower() : follow.getFollowed();
            if (user == null) {
                return null;
            }
            authors.add(new Author(user.getId(),user.getAvatar(), user.getUsername(), user.isStatus()));
        }
        return authors;
    }

    public User mostfolloweduser() {
        long id = followRepository.findMostFollowedUserId();
        User user = userService.getUserById(id).orElse(null);
        return user;
    }
}
