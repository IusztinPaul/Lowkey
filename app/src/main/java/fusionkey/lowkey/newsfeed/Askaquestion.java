package fusionkey.lowkey.newsfeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
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
        final String id = attributes.getUsername();
        final String uniqueID = attributes.getUserEmail();
        newsFeedRequest = new NewsFeedRequest(uniqueID);
        circleImageView.setImageBitmap(LowKeyApplication.profilePhoto);
        username.setText(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean anon = checkBox.isChecked();
                if(!title.getText().toString().equals("") && !body.getText().toString().equals("")){
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    newsFeedRequest.postQuestion(timestamp.getTime(),anon,String.valueOf(title.getText()),String.valueOf(body.getText()));
                    onBackPressed();
                }
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
