package Blog.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "followerid", nullable = false)
    private User follower;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "followedid", nullable = false)
    private User followed;

    public User getFollowed() {
        return followed;
    }

    public User getFollower() {
        return follower;
    }

    public long getId() {
        return id;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setId(long id) {
        this.id = id;
    }
}
