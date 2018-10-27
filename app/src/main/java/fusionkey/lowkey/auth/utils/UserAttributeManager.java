package fusionkey.lowkey.auth.utils;

import fusionkey.lowkey.auth.models.UserDB;

public class UserAttributeManager {
    private UserDB userDB;
    private String userEmail; // User email.

    public UserAttributeManager(String userEmail) {
        this.userEmail = userEmail;
    }

    private void getAttributes() {
        this.userDB = UserDBManager.getUserData(userEmail);
    }

    public String getUsername() {
        getAttributes();
        return userDB.getUsername();
    }

    public String getEmail() {
        return userEmail;
    }
}
