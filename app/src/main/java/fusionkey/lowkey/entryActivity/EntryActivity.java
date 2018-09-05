package fusionkey.lowkey.entryActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.LoginActivity;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.RegisterActivity;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.profile.EditUserActivity;

public class EntryActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        boolean isLogged = LowKeyApplication.userManager.logInIfHasCredentials(this,
                new AuthCallback() {
                    @Override
                    public void execute() {
                        //TODO: Add ProgressBar.
                        Intent myIntent = new Intent(EntryActivity.this, Main2Activity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        EntryActivity.this.startActivity(myIntent);
                    }
                });
        // If you are logged in just proceed.
       if(isLogged) return;

        Button Glogin = (Button) findViewById(R.id.Gconnect);
        Button Alogin = (Button) findViewById(R.id.Aconnect);
        Button Slogin = (Button) findViewById(R.id.reg);

        Slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0,0);
                Intent intent = new Intent(EntryActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        Glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EntryActivity.this, "Connect with GOOGLE +", Toast.LENGTH_LONG).show();
            }
        });

        Alogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0,0);
                Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        Glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(EntryActivity.this, gso);
               // mGoogleSignInClient.signOut();

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(EntryActivity.this);
                if(account != null) {
                    Intent intent = new Intent(EntryActivity.this, Main2Activity.class);
                    startActivity(intent);

                }
            }
        });
    }
    @Override
    public void onBackPressed(){

    }

}
