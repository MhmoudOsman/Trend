package mahmud.osman.trend.Models;

public class UserProfileModel {
    private String imageUri, name, email;

    public UserProfileModel() {
    }

    public UserProfileModel(String imageUri, String name, String email) {
        this.imageUri = imageUri;
        this.name = name;
        this.email = email;
    }

    public UserProfileModel(String name, String email) {
        this.name = name;
        this.email = email;
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
