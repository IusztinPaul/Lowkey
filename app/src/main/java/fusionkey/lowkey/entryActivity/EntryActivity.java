package fusionkey.lowkey.entryActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.LoadUserDataActivity;
import fusionkey.lowkey.auth.LoginActivity;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.RegisterActivity;
import fusionkey.lowkey.auth.utils.AuthCallback;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.NetworkManager;

public class EntryActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeClients();

        // If you are logged in just proceed.
        if (ifIsLoggedInLogIn()) return;

        // If is not logged in create view.
        setContentView(R.layout.activity_entry);
        pBar = findViewById(R.id.pBar);
        switchView(false);

        Button Glogin = (Button) findViewById(R.id.Gconnect);
        Button Alogin = (Button) findViewById(R.id.Aconnect);
        Button Slogin = (Button) findViewById(R.id.reg);

        Slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0, 0);
                Intent intent = new Intent(EntryActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Alogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ifIsLoggedInLogIn()) {
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
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
                if (account != null) {
                    Intent intent = new Intent(EntryActivity.this, Main2Activity.class);
                    startActivity(intent);

                }
            }
        });


    }

    @Override
    public void onBackPressed() {

    }

    private boolean ifIsLoggedInLogIn() {
        return NetworkManager.isNetworkAvailable() &&
                LowKeyApplication.userManager.logInIfHasCredentials(
                new AuthCallback() {
                    @Override
                    public void execute() {
                        Intent myIntent = new Intent(EntryActivity.this, LoadUserDataActivity.class);

                        String userEmail = LowKeyApplication.userManager.getCachedEmail();
                        myIntent.putExtra(UserAttributesEnum.EMAIL.toString(), userEmail);

                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        EntryActivity.this.startActivity(myIntent);
                    }
                });


    }

    private void switchView(boolean loading) {
        pBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void initializeClients() {
        AWSMobileClient.getInstance().initialize(this).execute();
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
    }
}
