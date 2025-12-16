package Blog.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity

@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id"),
        @UniqueConstraint(columnNames = "username")
})

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String avatar;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    private String role;
    private boolean status;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }

    public String getRole() {
        return role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @jakarta.persistence.OneToMany(mappedBy = "author", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Post> posts;

    @jakarta.persistence.OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Like> likes;

    @jakarta.persistence.OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Comment> comments;

    @jakarta.persistence.OneToMany(mappedBy = "follower", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Follow> following;

    @jakarta.persistence.OneToMany(mappedBy = "followed", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Follow> followers;

    @jakarta.persistence.OneToMany(mappedBy = "reporter", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<UserReport> reports;

    @jakarta.persistence.OneToMany(mappedBy = "reportedUser", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<UserReport> reported;

    @jakarta.persistence.OneToMany(mappedBy = "sender", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Notification> sentNotifications;

    @jakarta.persistence.OneToMany(mappedBy = "reciever", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Notification> receivedNotifications;

    @jakarta.persistence.OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Report> postReports;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
