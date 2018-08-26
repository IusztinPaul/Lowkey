package fusionkey.lowkey.login.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fusionkey.lowkey.R;

public class UserManager {

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

    public void login(final String email, final String password, final Activity activityFrom, final Class<? extends Activity> activityTo) {
        // Prepare the user object.
        cognitoPoolUtils.setUser(email);

        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.e("onSuccess", userSession.toString());
                cognitoPoolUtils.setUserSession(userSession);

                // Proceed to the main activity if everything it's ok.
                Intent myIntent = new Intent(activityFrom, activityTo);
                activityFrom.startActivity(myIntent);
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
                Toast.makeText(activityFrom, exception.getMessage(), Toast.LENGTH_SHORT).show();

                // It means that the login failed so the user object it's not valid.
                cognitoPoolUtils.setUserToNull();
            }
        };
        // Sign in the user
        cognitoPoolUtils.getUser().getSessionInBackground(authenticationHandler);
    }

    public boolean logout() {
        try {
            cognitoPoolUtils.getUser().signOut();
            cognitoPoolUtils.setAllUserDataToNull();
            return true;
        } catch (NullPointerException e) {
            // The user has to be set up manually in the cognitoPoolUtils object. So if it wasn't
            // the logout will not work (user = null).
            return false;
        }
    }

    public void register(String email, String password, HashMap<UserAttributesEnum, String> attributes, final Activity currentActivity) {

        // All the attributes have to be passed to the user.
        if(attributes.size() != UserAttributesEnum.values().length)
            throw new UserAttributeException("You have to pass all " + UserAttributesEnum.class.getName() + " attributes");

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
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
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(currentActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        cognitoPoolUtils.getUserPool().signUpInBackground(email, password, userAttributes, null, signUpCallback);
    }

    public void confirmRegistrationWithCode(String confirmationCode, final Activity currentActivity, final SuccessCallback callback) {

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

    public void requestConfirmationCode(final Activity currentActivity, final SuccessCallback callback) {
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

    public void getUserAttributes(final Activity currentActivity, final SuccessCallback callback) {
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

    public void updateUserAttributes(HashMap<UserAttributesEnum, String> attributes, final Activity currentActivity, final SuccessCallback callback) {

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

    public void changeUserPassword(String oldPassword, String newPassword, final Activity currentActivity, final SuccessCallback callback) {
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
}
