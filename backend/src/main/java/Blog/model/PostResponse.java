package Blog.model;

import java.util.Date;
import java.util.List;

public class PostResponse {

    private Long id;
    private String title;
    private Author author;
    private String content;
    private Media media;
    private Date timestamp;
    private int likes;
    private List<CommentResponse> comments;
    private boolean isLiked;
    private int cmts;
 

    // Constructors
    public PostResponse() {
    }

    // Getters and Setters
    public int getCmts() {
        return cmts;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
    public void setCmts(int cmts) {
        this.cmts = cmts;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }

    public boolean isLiked() {
        return isLiked;
    }



    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class Media {
        private String type;
        private String url;

        public Media() {
        }

        public Media(String type, String url) {
            this.type = type;
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
