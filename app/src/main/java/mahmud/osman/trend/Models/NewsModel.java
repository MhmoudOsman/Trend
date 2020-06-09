package mahmud.osman.trend.Models;

public class NewsModel {
    private String image_uri, title, subject, writer;
    private int type;
    private Object date;
    private  String video_url;
    private boolean box_checked;

    public NewsModel() {
    }
    //without video
    public NewsModel(String image_uri, String title, String subject, String writer, int type, Object date, boolean box_checked) {
        this.image_uri = image_uri;
        this.title = title;
        this.subject = subject;
        this.writer = writer;
        this.type = type;
        this.date = date;
        this.box_checked = box_checked;
    }

    //without subject
    public NewsModel(String image_uri, String title, String writer, int type, Object date, String video_url, boolean box_checked) {
        this.image_uri = image_uri;
        this.title = title;
        this.writer = writer;
        this.type = type;
        this.date = date;
        this.video_url = video_url;
        this.box_checked = box_checked;
    }

    //add all
    public NewsModel(String image_uri, String title, String subject, String writer, int type, Object date, String video_url, boolean box_checked) {
        this.image_uri = image_uri;
        this.title = title;
        this.subject = subject;
        this.writer = writer;
        this.type = type;
        this.date = date;
        this.video_url = video_url;
        this.box_checked = box_checked;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public boolean isBox_checked() {
        return box_checked;
    }

    public void setBox_checked(boolean box_checked) {
        this.box_checked = box_checked;
    }

}
