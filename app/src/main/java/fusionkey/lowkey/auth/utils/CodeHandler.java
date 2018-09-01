package fusionkey.lowkey.auth.utils;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;

public interface CodeHandler {
    void handle(ForgotPasswordContinuation continuation);
}
