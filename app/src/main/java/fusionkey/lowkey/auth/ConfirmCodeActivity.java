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

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.AttributesValidator;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.entryActivity.EntryActivity;

public class ConfirmCodeActivity extends AppCompatActivity {

    public static String STEP = "step";

    private LinearLayout llFirstStep;
    private LinearLayout llSecondStep;
    private EditText etEmail;
    private EditText etCode;
    private Button btnContinue;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);

        llFirstStep = findViewById(R.id.llFirstStep);
        llSecondStep = findViewById(R.id.llSecondStep);

        int step = getIntent().getIntExtra(STEP, 1);
        createLayout(step);
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

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                etEmail.setError(null);
                String email = etEmail.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    etEmail.setError(ConfirmCodeActivity.this.getResources().getString(R.string.field_empty));
                    etEmail.requestFocus();
                    return;
                }

                if(!AttributesValidator.isEmailValid(email)) {
                    etEmail.setError(ConfirmCodeActivity.this.getResources().getString(R.string.error_invalid_email));
                    etEmail.requestFocus();
                    return;
                }

                LowKeyApplication.userManager.setUser(email);
                createLayout(2);
            } else Toast.makeText(ConfirmCodeActivity.this, "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSecondStep() {
        llSecondStep.setVisibility(View.VISIBLE);
        llFirstStep.setVisibility(View.GONE);

        etCode = findViewById(R.id.etCode);
        btnFinish = findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                etCode.setError(null);
                String code = etCode.getText().toString();
                if(TextUtils.isEmpty(code)) {
                    etCode.setError(ConfirmCodeActivity.this.getResources().getString(R.string.field_empty));
                    etCode.requestFocus();
                    return;
                }

                LowKeyApplication.userManager.confirmRegistrationWithCode(code, ConfirmCodeActivity.this,
                        new AuthCallback() {
                            @Override
                            public void execute() {
                                Intent intent = new Intent(ConfirmCodeActivity.this, EntryActivity.class);
                                startActivity(intent);

                                Toast.makeText(ConfirmCodeActivity.this,
                                        ConfirmCodeActivity.this.getResources().getString(R.string.confirm_success_message),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });


            }
            else Toast.makeText(ConfirmCodeActivity.this, "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();
                }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
