package Blog.model;

public enum NotificationType {
    LIKE("liked your post"),
    FOLLOW("started following you"),
    POST("posted a new post"),
    COMMENT("commented on your post");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
