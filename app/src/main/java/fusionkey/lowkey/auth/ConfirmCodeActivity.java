package fusionkey.lowkey.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.main.Main2Activity;

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
        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
        llSecondStep.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setError(null);
                String email = etEmail.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    etEmail.setError(ConfirmCodeActivity.this.getResources().getString(R.string.field_empty));
                    etEmail.requestFocus();
                    return;
                }
                LowKeyApplication.loginManager.setUser(email);
                createLayout(2);
            }
        });
    }

    private void setupSecondStep() {
        llSecondStep.setVisibility(View.VISIBLE);
        etCode = findViewById(R.id.etCode);
        btnFinish = findViewById(R.id.btnFinish);
        llFirstStep.setVisibility(View.GONE);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCode.setError(null);
                String code = etCode.getText().toString();
                if(TextUtils.isEmpty(code)) {
                    etCode.setError(ConfirmCodeActivity.this.getResources().getString(R.string.field_empty));
                    etCode.requestFocus();
                    return;
                }

                LowKeyApplication.loginManager.confirmRegistrationWithCode(code, ConfirmCodeActivity.this,
                        new AuthCallback() {
                            @Override
                            public void execute() {
                                Intent intent = new Intent(ConfirmCodeActivity.this, Main2Activity.class);
                                startActivity(intent);
                            }
                        });
            }
        });
    }
}
