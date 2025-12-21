package Blog.model;

public class Author {
    private String avatar;
    private String username;
    private boolean status;
    private long id;
    private String role;

    public Author() {
    }

    public Author(long id, String avatar, String username, boolean status, String role) {
        this.avatar = avatar;
        this.username = username;
        this.status = status;
        this.id = id;
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
