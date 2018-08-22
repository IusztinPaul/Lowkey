package fusionkey.lowkey.entryActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fusionkey.lowkey.login.LoginActivity;
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

    }
}
