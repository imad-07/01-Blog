package Blog.model;

public class AdminPost {

    private Long id;
    private String title;
    private Author author;
    private String content;
    private int comments;
    private int likes;
    private boolean status;

    public AdminPost(Long id, String title, Author author, String content, int comments, int likes, boolean status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.comments = comments;
        this.likes = likes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isstatus() {
        return status;
    }

    public void setstatus(boolean status) {
        this.status = status;
    }
}
