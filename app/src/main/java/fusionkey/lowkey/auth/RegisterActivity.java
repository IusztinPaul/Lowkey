package fusionkey.lowkey.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.AttributesValidator;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.main.Main2Activity;

public class RegisterActivity extends AppCompatActivity {
    //TODO: Add TextView with password indications(what should contain)
    //TODO: AND ProgressBar when registering.

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPass;
    private EditText etPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        etPass2 = findViewById(R.id.etPassword2);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        etEmail.setError(null);
        etUsername.setError(null);
        etPass.setError(null);
        etPass2.setError(null);

        String email = etEmail.getText().toString(),
               username = etUsername.getText().toString(),
               password = etPass.getText().toString(),
               password2 = etPass2.getText().toString();

        if(!AttributesValidator.isEmailValid(email)) {
            etEmail.setError(getResources().getString(R.string.register_invalid_email));
            etEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(username)) {
            etUsername.setError(getResources().getString(R.string.register_username_empty));
            etUsername.requestFocus();
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

        HashMap<UserAttributesEnum, String> attributes = new HashMap<>();
        attributes.put(UserAttributesEnum.USERNAME, username);
        LowKeyApplication.loginManager.register(email, password, attributes, this,
                new AuthCallback() {
                    @Override
                    public void execute() {
                        Intent intent = new Intent(RegisterActivity.this, ConfirmCodeActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
