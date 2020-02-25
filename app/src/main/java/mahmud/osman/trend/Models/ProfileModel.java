package mahmud.osman.trend.Models;

public class ProfileModel {

    private String imageUri, name, email, mobile;

    public ProfileModel() {
    }

    public ProfileModel(String ImageUri, String Name, String Email, String Mobile) {

        this.imageUri = ImageUri;
        this.name = Name;
        this.email = Email;
        this.mobile = Mobile;

    }

    public ProfileModel(String imageUri , String name , String email) {
        this.imageUri = imageUri;
        this.name = name;
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {

        this.mobile = mobile;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
