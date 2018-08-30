package fusionkey.lowkey.entryActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import fusionkey.lowkey.auth.LoginActivity;
import fusionkey.lowkey.R;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        Button Glogin = (Button) findViewById(R.id.Gconnect);
        Button Alogin = (Button) findViewById(R.id.Aconnect);

        Glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EntryActivity.this, "Connect with GOOGLE +", Toast.LENGTH_LONG).show();
            }
        });

        Alogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

//        try {
//            GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//            AccountManager am = AccountManager.get(this);
//            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
//            String token = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0].name,
//                    "audience:server:client_id:879577828342-amqkg01j1c8lebc08uv22h45ctski6sp.apps.googleusercontent.com");
//            Map<String, String> logins = new HashMap<String, String>();
//            logins.put("accounts.google.com", token);
//            credentialsProvider.setLogins(logins);
//        } catch (Exception e) {
//
//        }


        Glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                // Build a GoogleSignInClient with the options specified by gso.
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(EntryActivity.this, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                EntryActivity.this.startActivityForResult(signInIntent, 0);
            }
        });
    }
}
