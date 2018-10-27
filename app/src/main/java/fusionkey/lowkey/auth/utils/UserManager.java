package fusionkey.lowkey.auth.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;

import java.util.HashMap;
import java.util.List;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;

public class UserManager {

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String USER_SHARED_PREFERENCES = "user_credentials";
    public static final String PASSWORD_SHARED_PREFERENCES = "password";

    private CognitoPoolUtils cognitoPoolUtils;
    private UserDB currentUser;

    private static UserManager instance;

    private UserManager(Context context) {
        this.cognitoPoolUtils = new CognitoPoolUtils(context);
    }

    public static UserManager getInstance(Context context) {
        if (instance == null)
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

                if (cacheCredentials)
                    cacheCredentials(email, password);

                if (onSuccessCallback != null)
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
                if (onFailCallback != null)
                    onFailCallback.execute();

                // It means that the login failed so the user object it's not valid.
                cognitoPoolUtils.setUserToNull();
            }
        };
        // Sign in the user
        cognitoPoolUtils.getUser().getSessionInBackground(authenticationHandler);
    }

    public String getCachedEmail() {
        SharedPreferences sharedPref =
                LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(UserAttributesEnum.EMAIL.toString(), null);
    }

    private boolean isLoggedIn() {
        return getCachedEmail() != null;
    }

    public boolean logInIfHasCredentials(AuthCallback onSuccessCallback) {
        if (isLoggedIn()) {
            SharedPreferences sharedPref =
                    LowKeyApplication.instance.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String email = sharedPref.getString(UserAttributesEnum.EMAIL.toString(), null);
            String password = sharedPref.getString(PASSWORD_SHARED_PREFERENCES, null);

            if (email != null && password != null)
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
            clearAllUserData();
            return true;
        } catch (NullPointerException e) {
            // The user has to be set up manually in the cognitoPoolUtils object. So if it wasn't
            // the logout will not work (user = null).
            return false;
        }
    }

    private void clearAllUserData() {
        cognitoPoolUtils.setAllUserDataToNull();
        this.currentUser = null;
        clearCredentials();
        LowKeyApplication.profilePhoto = null;
    }

    /**
     * @param email:           userId
     * @param password:        password
     * @param username:      username
     * @param currentActivity: for Toasts
     */
    public void register(final String email, String password, final String username,
                         final Activity currentActivity, final AuthCallback onSuccessCallback) {

        SignUpHandler signUpCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                cognitoPoolUtils.setUser(cognitoUser);
                setUpUser(email, username);
                if (!userConfirmed) {
                    Toast.makeText(currentActivity,
                            currentActivity.getResources().getString(R.string.register_validation_message),
                            Toast.LENGTH_SHORT).show();
                }

                if (onSuccessCallback != null)
                    onSuccessCallback.execute();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("onFailure", exception.getMessage());
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        cognitoPoolUtils.getUserPool().signUpInBackground(email, password, userAttributes, null, signUpCallback);
    }

    private void setUpUser(String email, String username) {
        this.currentUser = new UserDB(email, username);
        UserDBManager.create(email, username);
    }

    public void confirmRegistrationWithCode(String confirmationCode, final Activity currentActivity, final AuthCallback onSuccessCallback) {

        GenericHandler confirmationCallback = new GenericHandler() {
            @Override
            public void onSuccess() {
                if (onSuccessCallback != null)
                    onSuccessCallback.execute();
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
                if (callback != null)
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

    public void requestUserDetails(String email, final AuthCallback callback) {
        String checkedEmail;

        if(email != null)
            checkedEmail = email;
        else if(currentUser != null)
            checkedEmail = currentUser.getUserEmail();
        else
            throw new RuntimeException("Current user or email is null! " +
                    "Can't update it's attributes with no email");

        currentUser = UserDBManager.getUserData(checkedEmail);
        if(currentUser == null)
            throw new RuntimeException("The requested user does not exist!");

        if(callback != null)
            callback.execute();
    }

    public void updateUserAttributes(HashMap<UserAttributesEnum, String> attributes,
                                     final AuthCallback successCallback) {
        UserDBManager.update(attributes);
        if(successCallback != null)
            successCallback.execute();
    }

    public void changeUserPassword(String oldPassword, String newPassword,
                                   final AuthCallback onFailCallback, final AuthCallback onSuccessCallback) {
        GenericHandler handler = new GenericHandler() {
            @Override
            public void onSuccess() {
                if (onSuccessCallback != null)
                    onSuccessCallback.execute();
            }

            @Override
            public void onFailure(final Exception exception) {
                Log.e("changePassword", exception.getMessage());
                if(onFailCallback != null)
                    onFailCallback.execute();
            }
        };
        cognitoPoolUtils.getUser().changePasswordInBackground(oldPassword, newPassword, handler);
    }

    public void requestPasswordForgot(final CodeHandler codeHandler,
                                      final AuthCallback onSuccessCallback,
                                      final Activity activity) {
        if (codeHandler != null) {
            final ForgotPasswordHandler handler = new ForgotPasswordHandler() {
                @Override
                public void onSuccess() {
                    Log.e("onSuccess", "Success");
                    if (onSuccessCallback != null)
                        onSuccessCallback.execute();
                }

                @Override
                public void getResetCode(final ForgotPasswordContinuation continuation) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // This handler has to set up the code and new password.
                            codeHandler.handle(continuation);
                            continuation.continueTask();
                        }
                    }).start();
                }

                public void onFailure(Exception exception) {
                    Log.e("onFailureForgotPass", exception.getMessage());
                    Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            cognitoPoolUtils.getUser().forgotPasswordInBackground(handler);
        }
    }

    public String getPhotoFileName() {
        return getParsedUserEmail();
    }

    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getUserEmail() : null;
    }

    public String getParsedUserEmail() {
        String email = getCurrentUserEmail();

        if(email == null)
            throw new RuntimeException("There is no email on current user" +
                ". Something is wrong!");

        return parseEmailToPhotoFileName(email);
    }

    public static String parseEmailToPhotoFileName(String email) {
        return email.replace("@", "").replace(".", "");
    }

    public UserDB getUserDetails() {
        return this.currentUser;
    }

    public String getAccessToken() {
        return cognitoPoolUtils.getUserSession().getAccessToken().getJWTToken();
    }

    public String getIdToken() {
        return cognitoPoolUtils.getUserSession().getIdToken().getJWTToken();
    }

    public String getUserId() {
        return cognitoPoolUtils.getUser().getUserId();
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

    public CognitoUserSession getSession() {
        return cognitoPoolUtils.getUserSession();
    }

    public void setUser(String userId) {
        cognitoPoolUtils.setUser(userId);
    }
}
