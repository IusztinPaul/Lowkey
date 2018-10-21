package fusionkey.lowkey.auth.utils;

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
/** @TODO @PAUL
 * java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
at java.util.ArrayList.get(ArrayList.java:411)
at fusionkey.lowkey.auth.utils.UserAttributeManager.getAttribute(UserAttributeManager.java:20)
at fusionkey.lowkey.auth.utils.UserAttributeManager.getUsername(UserAttributeManager.java:31)
at fusionkey.lowkey.newsfeed.asynctasks.NewsFeedAsyncTask$1.onResponse(NewsFeedAsyncTask.java:140)
at fusionkey.lowkey.newsfeed.util.NewsFeedRequest$4.onResponse(NewsFeedRequest.java:122)
at fusionkey.lowkey.newsfeed.util.NewsFeedRequest$4.onResponse(NewsFeedRequest.java:117)
at com.android.volley.toolbox.JsonRequest.deliverResponse(JsonRequest.java:90)
at com.android.volley.ExecutorDelivery$ResponseDeliveryRunnable.run(ExecutorDelivery.java:102)
at android.os.Handler.handleCallback(Handler.java:751)
at android.os.Handler.dispatchMessage(Handler.java:95)
at android.os.Looper.loop(Looper.java:154)
at android.app.ActivityThread.main(ActivityThread.java:6776)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1496)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1386)
 */
        if (userAttributes == null) {
            List<UserType> users = LowKeyApplication.
                    userManager.getUsers(UserAttributesEnum.EMAIL, userEmail);

            // User email it's unique so the list will have length 1 or
            // 0 if there is no user with that email.
            if (users != null && users.size() > 0)
                userAttributes = users.get(0);
        }

        if (userAttributes != null)
            for (AttributeType a : userAttributes.getAttributes())
                if (a.getName().equals(attribute.toString()))
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
