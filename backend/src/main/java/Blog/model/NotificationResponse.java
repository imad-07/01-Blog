package Blog.model;

import java.time.LocalDateTime;

public class NotificationResponse {
    private long id;
    private Author sender;
    private String content;
    private boolean seen;
    private LocalDateTime createdAt;

    public String getContent() {
        return content;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Author getSender() {
        return sender;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setSender(Author sender) {
        this.sender = sender;
    }
}
