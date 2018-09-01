package fusionkey.lowkey.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;

public class GetStarted extends AppCompatActivity {
    ProgressBar progressBar;



    public static final String currentUser = "sefullabani";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        Boolean listenerString = intent.getBooleanExtra("Listener",true);

        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser,this,progressBar,listenerString);
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
