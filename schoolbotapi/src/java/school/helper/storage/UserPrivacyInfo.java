package school.helper.storage;

public class UserPrivacyInfo {
    private String username;
    private String password;
    public UserPrivacyInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
