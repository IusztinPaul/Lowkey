package fusionkey.lowkey.auth.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.LoginActivity;

public class UserManager {

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String USER_SHARED_PREFERENCES = "user_credentials";
    public static final String PASSWORD_SHARED_PREFERENCES = "password";

    private CognitoPoolUtils cognitoPoolUtils;

    private static UserManager instance;

    private UserManager(Context context) {
        this.cognitoPoolUtils = new CognitoPoolUtils(context);
    }

    public static UserManager getInstance(Context context) {
        if(instance == null)
            instance = new UserManager(context);

        return instance;
    }

    public void login(final String email, final String password,
                      final AuthCallback onSuccessCallback,
                      final AuthCallback onFailCallback,
                      final boolean cacheCredentials) {
        // Prepare the user object.
        cognitoPoolUtils.setUser(email);

        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.e("onSuccess", userSession.toString());
                cognitoPoolUtils.setUserSession(userSession);
                if(cacheCredentials)
                    cacheCredentials(email, password);

                if(onSuccessCallback != null)
                    onSuccessCallback.execute();

            }
            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                Log.e("getAuthenticationDeta", "Validating auth details.");
                authenticationContinuation.continueTask();
            }
            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                multiFactorAuthenticationContinuation.continueTask();
            }
            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                continuation.continueTask();
            }
            @Override
            public void onFailure(Exception exception) {
                if(onFailCallback != null)
                    onFailCallback.execute();

                // It means that the login failed so the user object it's not valid.
                cognitoPoolUtils.setUserToNull();
            }
        };
        // Sign in the user
        cognitoPoolUtils.getUser().getSessionInBackground(authenticationHandler);
    }

    private boolean isLoggedIn(Activity activity) {
        SharedPreferences sharedPref =
                LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String email = sharedPref.getString(UserAttributesEnum.EMAIL.toString(), null);
        return email != null;
    }

    public boolean logInIfHasCredentials(Activity activity, AuthCallback onSuccessCallback) {
        if(isLoggedIn(activity)) {
            SharedPreferences sharedPref =
                    LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String email = sharedPref.getString(UserAttributesEnum.EMAIL.toString(), null);
            String password = sharedPref.getString(PASSWORD_SHARED_PREFERENCES, null);

            if(email != null && password != null)
                login(email, password, onSuccessCallback, null, false);

            return true;
        }
        return false;
    }

    private void cacheCredentials(String email, String password) {
        SharedPreferences sharedPref =
                LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UserAttributesEnum.EMAIL.toString(), email);
        editor.putString(PASSWORD_SHARED_PREFERENCES, password);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences sharedPref =
                LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UserAttributesEnum.EMAIL.toString(), null);
        editor.putString(PASSWORD_SHARED_PREFERENCES, null);
        editor.apply();
    }

    public boolean logout() {
        try {
            cognitoPoolUtils.getUser().signOut();
            cognitoPoolUtils.setAllUserDataToNull();
            clearCredentials();
            return true;
        } catch (NullPointerException e) {
            // The user has to be set up manually in the cognitoPoolUtils object. So if it wasn't
            // the logout will not work (user = null).
            return false;
        }
    }

    /**
     * @param email: userId
     * @param password: password
     * @param attributes: Set to 'null' if no user attributes are required.
     * @param currentActivity: for Toasts
     */
    public void register(String email, String password, HashMap<UserAttributesEnum, String> attributes,
                         final Activity currentActivity, final AuthCallback onSuccessCallback) {
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        // Add an empty string to the attributes that were not added -> AWS does not like them empty.
        Set<UserAttributesEnum> allAttributes = new HashSet<>(Arrays.asList(UserAttributesEnum.values()));
        if(attributes != null)
            allAttributes.removeAll(attributes.keySet());
        for(UserAttributesEnum attribute : allAttributes)
            userAttributes.addAttribute(attribute.toString(), "");

        // Now add the attributes that have a actual value.
        if(attributes != null)
            for(Map.Entry<UserAttributesEnum, String> entry : attributes.entrySet())
                userAttributes.addAttribute(entry.getKey().toString(), entry.getValue());

        SignUpHandler signUpCallback = new SignUpHandler() {

            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                cognitoPoolUtils.setUser(cognitoUser);

                if(!userConfirmed) {
                    Toast.makeText(currentActivity,
                            currentActivity.getResources().getString(R.string.register_validation_message),
                            Toast.LENGTH_SHORT).show();
                }

                if(onSuccessCallback != null)
                    onSuccessCallback.execute();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("onFailure", exception.getMessage());
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        cognitoPoolUtils.getUserPool().signUpInBackground(email, password, userAttributes, null, signUpCallback);
    }

    public void confirmRegistrationWithCode(String confirmationCode, final Activity currentActivity, final AuthCallback callback) {

        GenericHandler confirmationCallback = new GenericHandler() {
            @Override
            public void onSuccess() {
                if(callback != null)
                    callback.execute();
            }
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // forcedAliasCreation = false: will cause confirmation to fail if the user email has been verified for another user in the same pool
        cognitoPoolUtils.getUser().confirmSignUpInBackground(confirmationCode, false, confirmationCallback);
    }

    public void requestConfirmationCode(final Activity currentActivity, final AuthCallback callback) {
        VerificationHandler handler = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                if(callback != null)
                    callback.execute();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        cognitoPoolUtils.getUser().resendConfirmationCodeInBackground(handler);
    }

    public boolean isUser() {
        return cognitoPoolUtils.getUser() != null;
    }

    public void getUserAttributes(final Activity currentActivity, final AuthCallback callback) {
        GetDetailsHandler handler = new GetDetailsHandler() {
            @Override
            public void onSuccess(final CognitoUserDetails list) {
                if(callback != null)
                    callback.execute();

                cognitoPoolUtils.setUserDetails(list);
            }

            @Override
            public void onFailure(final Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        cognitoPoolUtils.getUser().getDetailsInBackground(handler);
    }

    public void updateUserAttributes(HashMap<UserAttributesEnum, String> attributes, final Activity currentActivity, final AuthCallback callback) {

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        for(Map.Entry<UserAttributesEnum, String> entry : attributes.entrySet())
            userAttributes.addAttribute(entry.getKey().toString(), entry.getValue());

        UpdateAttributesHandler handler = new UpdateAttributesHandler() {
            @Override
            public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                if(callback != null)
                    callback.execute();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        cognitoPoolUtils.getUser().updateAttributesInBackground(userAttributes, handler);
    }

    public void changeUserPassword(String oldPassword, String newPassword, final Activity currentActivity, final AuthCallback callback) {
        GenericHandler handler = new GenericHandler() {
            @Override
            public void onSuccess() {
                if(callback != null)
                    callback.execute();
            }

            @Override
            public void onFailure(final Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        cognitoPoolUtils.getUser().changePasswordInBackground(oldPassword, newPassword, handler);
    }

    public String getAccessToken() {
        return cognitoPoolUtils.getUserSession().getAccessToken().getJWTToken();
    }

    public String getIdToken() {
        return cognitoPoolUtils.getUserSession().getIdToken().getJWTToken();
    }

    /**
     * Pass null to both values if you don't want any filtering.
     */
    public List<UserType> getUsers(UserAttributesEnum attribute, String value) {
        return cognitoPoolUtils.getUsers(attribute, value);
    }

    public CognitoUser getUser() {
        return cognitoPoolUtils.getUser();
    }

    public CognitoUserDetails getUserDetails() {
        return cognitoPoolUtils.getUserDetails();
    }

    public CognitoUserSession getSession() {
        return cognitoPoolUtils.getUserSession();
    }
}
