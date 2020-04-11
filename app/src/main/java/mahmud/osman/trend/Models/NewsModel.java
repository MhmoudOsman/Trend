package mahmud.osman.trend.Models;

public class NewsModel {
    private String image_uri, title, subject, writer,type;
    private Object date;

    public NewsModel() {
    }

    public NewsModel(String image_uri, String title, String subject, Object date, String writer, String type) {
        this.image_uri = image_uri;
        this.title = title;
        this.subject = subject;
        this.writer = writer;
        this.date = date;
        this.type = type;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Object getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
