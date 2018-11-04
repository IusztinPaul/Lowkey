package fusionkey.lowkey.newsfeed;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

public class AskAQuestion extends AppCompatActivity {
    CircleImageView circleImageView;
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
        circleImageView.setImageBitmap(LowKeyApplication.userManager.profilePhoto);
        username.setText(id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkManager.isNetworkAvailable()) {
                    boolean anon = checkBox.isChecked();
                    String titleText = title.getText().toString().trim();
                    String bodyText = body.getText().toString().trim();

                    if (isPostDataOk(titleText, bodyText)) {
                        Intent retrieveData = new Intent();
                        retrieveData.putExtra("TitleQ", titleText);
                        retrieveData.putExtra("BodyQ", bodyText);
                        retrieveData.putExtra("TimestampQ", timestamp.getTime());
                        retrieveData.putExtra("anonQ", anon);
                        setResult(Activity.RESULT_OK, retrieveData);

                        onBackPressed();
                    }
                } else
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.no_network_message),
                            Toast.LENGTH_SHORT).show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean isPostDataOk(String titleText, String bodyText) {
        return !TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(bodyText);
    }

}
