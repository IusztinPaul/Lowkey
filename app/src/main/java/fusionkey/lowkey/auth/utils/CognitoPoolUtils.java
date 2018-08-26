package fusionkey.lowkey.auth.utils;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;

public class CognitoPoolUtils {

    private CognitoUserPool userPool;
    private CognitoUser user;
    private CognitoUserDetails userDetails;
    private CognitoUserSession userSession;

    CognitoPoolUtils (Context context)  {
        userPool = new CognitoUserPool(context,
                "eu-central-1_JoEaRznTH",
                "5s1djmjafqttgk2jb82l3ng3eo",
                "tr02eavc68bv2fne6u0bd4mli7trdbmnqicjk6t3o4k2authga",
                Regions.EU_CENTRAL_1
        );
    }

    public CognitoUserPool getUserPool() {
        return userPool;
    }

    public CognitoUser getUser() {
        return user;
    }

    public synchronized void setUser(String userId) {
        this.user = userPool.getUser(userId);
    }

    public synchronized void setUser(CognitoUser user) {
        this.user = user;
    }

    public synchronized void setUserToNull() {
        this.user = null;
    }

    public CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public synchronized void setUserDetails(CognitoUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public CognitoUserSession getUserSession() {
        return userSession;
    }

    public synchronized void setUserSession(CognitoUserSession cognitoUserSession) {
        this.userSession = cognitoUserSession;
    }

    public void setAllUserDataToNull() {
        user = null;
        userDetails = null;
        userSession = null;
    }
}
