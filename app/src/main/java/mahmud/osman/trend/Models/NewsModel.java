package mahmud.osman.trend.Models;

public class NewsModel {
    private String image_uri, titl, subject, writer, date,type;

    public NewsModel() {
    }

    public NewsModel(String image_uri, String titl, String subject, String date, String writer, String type) {
        this.image_uri = image_uri;
        this.titl = titl;
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

    public String getTitl() {
        return titl;
    }

    public void setTitl(String titl) {
        this.titl = titl;
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

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
