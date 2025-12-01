package Blog.model;

public class ReportPost {

    private Long id;
    private String title;
    private Author author;
    private String content;

    public ReportPost(long id, String title, String content, Author author) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.author = author;
}


    public Author getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
