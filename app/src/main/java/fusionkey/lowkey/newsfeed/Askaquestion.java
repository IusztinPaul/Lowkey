package fusionkey.lowkey.newsfeed;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

public class Askaquestion extends AppCompatActivity {
    CircleImageView circleImageView;
    TextView time;
    TextView title;
    TextView body;
    TextView username;
    Button button;
    Button back;
    NewsFeedRequest newsFeedRequest;
    CheckBox checkBox;
    Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askaquestion);
        circleImageView = findViewById(R.id.circleImageView);
        title = findViewById(R.id.Title);
        body = findViewById(R.id.lastmsg);
        username = findViewById(R.id.name);
        button = findViewById(R.id.email_sign_in_button3);
        checkBox = findViewById(R.id.checkBox);
        back = findViewById(R.id.email_sign_in_button2);
        UserDB attributes = LowKeyApplication.userManager.getUserDetails();
        timestamp = new Timestamp(System.currentTimeMillis());
        final String id = attributes.getUsername();
        final String uniqueID = attributes.getUserEmail();
        newsFeedRequest = new NewsFeedRequest(uniqueID);
        circleImageView.setImageBitmap(LowKeyApplication.profilePhoto);
        username.setText(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkManager.isNetworkAvailable()) {
                    boolean anon = checkBox.isChecked();
                    if (!title.getText().toString().equals("") && !body.getText().toString().equals("")) {

                        newsFeedRequest.postQuestion(timestamp.getTime(), anon, String.valueOf(title.getText()), String.valueOf(body.getText()));

                            Intent retrieveData = new Intent();
                            retrieveData.putExtra("TitleQ", String.valueOf(title.getText()));
                            retrieveData.putExtra("BodyQ", String.valueOf(body.getText()));
                            retrieveData.putExtra("TimestampQ", timestamp.getTime());
                            retrieveData.putExtra("anonQ", anon);
                            setResult(Activity.RESULT_OK, retrieveData);


                        onBackPressed();

                    }
                } else Toast.makeText(getApplicationContext(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

        }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                }
            });




    }



}
