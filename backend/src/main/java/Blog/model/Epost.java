package Blog.model;

import org.springframework.web.multipart.MultipartFile;

public class Epost {
    String content;
    String title;
    MultipartFile media;
    long id;

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public MultipartFile getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMedia(MultipartFile media) {
        this.media = media;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private Boolean deleteMedia;

    public Boolean getDeleteMedia() {
        return deleteMedia;
    }

    public void setDeleteMedia(Boolean deleteMedia) {
        this.deleteMedia = deleteMedia;
    }
}
