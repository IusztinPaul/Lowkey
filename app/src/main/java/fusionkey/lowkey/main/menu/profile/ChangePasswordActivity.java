package fusionkey.lowkey.main.menu.profile;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.AttributesValidator;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.NetworkManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPass;
    private EditText etNewPass1;
    private EditText etNewPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPass = findViewById(R.id.etOldPass);
        etNewPass1 = findViewById(R.id.etNewPass);
        etNewPass2 = findViewById(R.id.etNewPass2);
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        ConstraintLayout back = findViewById(R.id.backL);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void save() {
        if(NetworkManager.isNetworkAvailable()) {
            etOldPass.setError(null);
            etNewPass1.setError(null);
            etNewPass2.setError(null);

            String oldPass = etOldPass.getText().toString().trim(),
                    newPass1 = etNewPass1.getText().toString().trim(),
                    newPass2 = etNewPass2.getText().toString().trim();

            if (!AttributesValidator.isPasswordValid(newPass1)) {
                etNewPass1.setError(getResources().getString(R.string.register_invalid_password_error));
                Toast.makeText(this,
                        getResources().getString(R.string.register_invalid_password_toast), Toast.LENGTH_SHORT).show();
                etNewPass1.requestFocus();
                return;
            }

            if (!newPass1.equals(newPass2)) {
                etNewPass1.setError(getResources().getString(R.string.register_passwords_not_match));
                etNewPass2.setError(getResources().getString(R.string.register_passwords_not_match));
                return;
            }

            if (oldPass.equals(newPass1)) {
                etOldPass.setError(getResources().getString(R.string.forgot_p_old_new_pass_equal_error));
                etNewPass1.setError(getResources().getString(R.string.forgot_p_old_new_pass_equal_error));
                etNewPass2.setError(getResources().getString(R.string.forgot_p_old_new_pass_equal_error));

                Toast.makeText(this,
                        getResources().getString(R.string.forgot_p_old_new_pass_equal_toast), Toast.LENGTH_SHORT).show();
                return;
            }

            LowKeyApplication.userManager.changeUserPassword(oldPass, newPass1, new AuthCallback() {
                        @Override
                        public void execute() {
                            etOldPass.setError(getResources().getString(R.string.forgot_p_old_pass_wrong_error));
                            Toast.makeText(ChangePasswordActivity.this,
                                    getResources().getString(R.string.forgot_p_old_pass_wrong_toast), Toast.LENGTH_SHORT).show();
                        }
                    }, new AuthCallback() {
                        @Override
                        public void execute() {
                            Toast.makeText(ChangePasswordActivity.this,
                                    getResources().getString(R.string.forgot_p_on_success_message_), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ChangePasswordActivity.this, Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
            );
        } else {
            Toast.makeText(this,
                    this.getString(R.string.no_network_message),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
