package fusionkey.lowkey.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.AttributesValidator;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.auth.utils.CodeHandler;
import fusionkey.lowkey.entryActivity.EntryActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private LinearLayout llFirstStep;
    private LinearLayout llSecondStep;
    private EditText etEmail;
    private EditText etCode;
    private EditText etPass;
    private EditText etPass2;
    private Button btnContinue;
    private Button btnFinish;

    private boolean proceedToCode = false;
    private String code;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        llFirstStep = findViewById(R.id.llFirstStep);
        llSecondStep = findViewById(R.id.llSecondStep);

        createLayout(1);
    }

    private void createLayout(int step) {
        switch (step) {
            case 1:
                setupFirstStep();
                break;
            case 2:
                setupSecondStep();
                break;
            default:
                llSecondStep.setVisibility(View.GONE);
                llFirstStep.setVisibility(View.GONE);
        }
    }

    private void setupFirstStep() {
        llFirstStep.setVisibility(View.VISIBLE);
        llSecondStep.setVisibility(View.GONE);

        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);

        if(isNetworkAvailable()){
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setError(null);
                String email = etEmail.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    etEmail.setError(ForgotPasswordActivity.this.getResources().getString(R.string.field_empty));
                    etEmail.requestFocus();
                    return;
                }

                if(!AttributesValidator.isEmailValid(email)) {
                    etEmail.setError(ForgotPasswordActivity.this.getResources().getString(R.string.error_invalid_email));
                    etEmail.requestFocus();
                    return;
                }

                Toast.makeText(ForgotPasswordActivity.this,
                        getResources().getString(R.string.forgot_check_email_message),
                        Toast.LENGTH_SHORT).show();

                LowKeyApplication.userManager.setUser(email);
                createLayout(2);
            }
        });}
    else Toast.makeText(ForgotPasswordActivity.this, "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

    }

    private void setupSecondStep() {
        llSecondStep.setVisibility(View.VISIBLE);
        llFirstStep.setVisibility(View.GONE);

        etCode = findViewById(R.id.etCode);
        etPass = findViewById(R.id.etPassword);
        etPass2 = findViewById(R.id.etPassword2);
        btnFinish = findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                validateCode();
                else Toast.makeText(ForgotPasswordActivity.this, "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

            }
        });

        LowKeyApplication.userManager.requestPasswordForgot(new CodeHandler() {
            @Override
            public void handle(ForgotPasswordContinuation continuation) {
                while (!proceedToCode);

                // Code and password are validated by the button. Here we just set it.
                continuation.setPassword(password);
                continuation.setVerificationCode(code);
            }
        }, new AuthCallback() {
            @Override
            public void execute() {
                Toast.makeText(ForgotPasswordActivity.this,
                        ForgotPasswordActivity.this.getResources().getString(R.string.forgot_successfully_message),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ForgotPasswordActivity.this, EntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, this);
    }

    private void validateCode() {
        etCode.setError(null);
        etPass.setError(null);
        etPass2.setError(null);

        String code = etCode.getText().toString();
        String password = etPass.getText().toString();
        String password2 = etPass2.getText().toString();

        if(TextUtils.isEmpty(code)) {
            etCode.setError(getResources().getString(R.string.field_empty));
            etCode.requestFocus();
            return;
        }

        if(!AttributesValidator.isPasswordValid(password)) {
            etPass.setError(getResources().getString(R.string.register_invalid_password_error));
            etPass.requestFocus();
            Toast.makeText(this, getResources().getString(R.string.register_invalid_password_toast), Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(password2)) {
            etPass.setError(getResources().getString(R.string.register_passwords_not_match));
            etPass2.setError(getResources().getString(R.string.register_passwords_not_match));
            etPass.requestFocus();
            return;
        }

        // Prepare data for the change password request.
        this.code = code;
        this.password = password;
        proceedToCode = true;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
