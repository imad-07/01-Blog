package Blog.model;

public class CommentResponse {
    private String content;
    private long id;
    private Author author;

    public Author getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public long getId() {
        return id;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }
}