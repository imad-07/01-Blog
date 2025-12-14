package Blog.model;

public class ReportResponse {
    private long id;
    private Author author;
    private ReportReason reason;
    private ReportPost post;
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public ReportPost getPost() {
        return post;
    }

    public Author getAuthor() {
        return author;
    }

    public long getId() {
        return id;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setPost(ReportPost post) {
        this.post = post;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }
}
