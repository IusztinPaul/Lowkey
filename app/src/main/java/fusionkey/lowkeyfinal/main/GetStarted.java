package fusionkey.lowkeyfinal.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import fusionkey.lowkeyfinal.R;
import fusionkey.lowkeyfinal.entryActivity.EntryActivity;
import fusionkey.lowkeyfinal.login.LoginActivity;

public class GetStarted extends AppCompatActivity {
    ProgressBar progressBar;
    public static final String currentUser = "USER1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser,this,progressBar,false);
        loadingAsyncTask.execute();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingAsyncTask.cancel(true);
                Intent intent = new Intent(GetStarted.this, Main2Activity.class);
                startActivity(intent);
            }
        });


    }
}
