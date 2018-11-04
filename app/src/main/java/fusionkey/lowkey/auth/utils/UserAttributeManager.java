package fusionkey.lowkey.auth.utils;

import java.util.Map;

import fusionkey.lowkey.auth.models.UserDB;

/**
 * Class that queries a user entry from DynamoDB.
 */
public class UserAttributeManager {
    private UserDB userDB;
    private String userEmail; // User email.

    public UserAttributeManager(String userEmail) {
        this.userEmail = userEmail;
        getAttributes();
    }

    public UserAttributeManager(UserDB userDB) {
        this.userEmail = userDB.getUserEmail();
        this.userDB = userDB;
    }

    private void getAttributes() {
        this.userDB = UserDBManager.getUserData(userEmail);
    }

    public void updateUserAttributes(final AuthCallback successCallback) {
        UserDBManager.update(userDB);
        if(successCallback != null)
            successCallback.execute();
    }

    public static void updateUserAttributes(Map<UserAttributesEnum, String> attributes,
                                     final AuthCallback successCallback) {
        UserDBManager.update(attributes);
        if(successCallback != null)
            successCallback.execute();
    }

    public static void updateUserAttributes(UserDB userDB,
                                            final AuthCallback successCallback) {
        UserDBManager.update(userDB);
        if(successCallback != null)
            successCallback.execute();
    }

    public String getUsername() {
        try {
            return userDB.getUsername();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getParsedEmail() {
        return userDB.getParsedEmail();
    }

    public String getEmail() {
        return userEmail;
    }

    public UserDB getUserDB() {
        return userDB;
    }

    public void setUserDB(UserDB userDB) {
        this.userDB = userDB;
    }
}
