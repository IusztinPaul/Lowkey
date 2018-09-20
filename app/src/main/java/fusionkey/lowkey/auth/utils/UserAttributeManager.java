package fusionkey.lowkey.auth.utils;

import android.util.Log;

import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;

import java.util.List;

import fusionkey.lowkey.LowKeyApplication;

public class UserAttributeManager {
    private UserType userAttributes;
    private String userEmail; // User email.

    public UserAttributeManager(String userEmail) {
        this.userEmail = userEmail;
    }

    private String getAttribute(UserAttributesEnum attribute) {

        if(userAttributes == null) {
            userAttributes = LowKeyApplication.
                    userManager.getUsers(UserAttributesEnum.EMAIL, userEmail).get(0);
        }

        for(AttributeType a : userAttributes.getAttributes())
            if(a.getName().equals(attribute.toString()))
                return a.getValue();

        return null;
    }

    public String getUsername() {
        return getAttribute(UserAttributesEnum.USERNAME);
    }

    public String getEmail() {
        return userEmail;
    }
}
