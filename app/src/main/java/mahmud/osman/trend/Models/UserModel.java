package mahmud.osman.trend.Models;

public class UserModel {

    private String imageUri, name, email;

    public UserModel(){}

    public UserModel(String ImageUri, String Name, String Email) {

        this.imageUri = ImageUri;
        this.name = Name;
        this.email = Email;

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
