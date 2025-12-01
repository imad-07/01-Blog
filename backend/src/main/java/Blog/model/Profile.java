package Blog.model;

import java.util.List;

public class Profile {
    private List<Author> followers;
    private List<Author> following;
    private List<PostResponse> posts;
    private Author user;
    private boolean followed;

    public List<Author> getFollowing() {
        return following;
    }

    public List<Author> getFollowers() {
        return followers;
    }

    public Author getUser() {
        return user;
    }

    public boolean getFollowed() {
        return followed;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setFollowing(List<Author> following) {
        this.following = following;
    }

    public void setFollowers(List<Author> followers) {
        this.followers = followers;
    }

    public void setuser(Author user) {
        this.user = user;
    }
    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}
