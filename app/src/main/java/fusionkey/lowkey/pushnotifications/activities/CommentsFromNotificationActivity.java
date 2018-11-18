package fusionkey.lowkey.pushnotifications.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.listAdapters.CommentAdapters.CommentAdapter;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.newsfeed.MyParcelable;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.asynctasks.PushNotificationsAsyncTask;
import fusionkey.lowkey.pushnotifications.requestUtils.NotificationRequest;
import fusionkey.lowkey.pushnotifications.service.IntentMappingSharredPrefferences;

public class CommentsFromNotificationActivity extends AppCompatActivity {


    public static NewsFeedMessage newsFeedMessage;

    TextView body;
    TextView posted;
    TextView title;
    CoordinatorLayout contentRoot;
    RecyclerView rvComments;
    Button button;
    EditText inputTxt;
    LinearLayout llAddComment;
    CardView questionInfo;
    CircleImageView imagepic;
    ArrayList<Comment> commentArrayList = new ArrayList<>();
    ArrayList<Comment> commentsSentList = new ArrayList<>();
    private CommentAdapter commentsAdapter = new CommentAdapter(commentArrayList, this);
    private PushNotificationsAsyncTask pushNotificationsAsyncTask;
    private NotificationRequest notificationRequest;
    public static String snsTOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_layout);

        contentRoot = findViewById(R.id.contentRoot);
        rvComments = findViewById(R.id.rvComments);
        llAddComment = findViewById(R.id.llAddComment);
        button = findViewById(R.id.sendComment);
        title = findViewById(R.id.username2);
        body = findViewById(R.id.body);
        questionInfo = findViewById(R.id.cardView10);
        imagepic = findViewById(R.id.circleImageView4);
        posted = findViewById(R.id.posted);

        inputTxt = findViewById(R.id.chat_input_msg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);

        rvComments.setAdapter(commentsAdapter);

        populateWithData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(inputTxt.getText().toString())) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    UserDB attributes = LowKeyApplication.userManager.getUserDetails();
                    final String uniqueID = attributes.getUserEmail();
                    final String username = attributes.getUsername();
                    new NewsFeedRequest(uniqueID).postComment(Long.parseLong(getIntent().getStringExtra("timestamp")), true, username, inputTxt.getText().toString(), snsTOPIC, LowKeyApplication.endpointArn);
                    commentArrayList.add(new Comment("true", String.valueOf(timestamp.getTime()), username, inputTxt.getText().toString(), uniqueID));

                    int newMsgPosition = commentArrayList.size() - 1;
                    commentsAdapter.notifyItemInserted(newMsgPosition);
                    commentsAdapter.notifyDataSetChanged();
                    rvComments.scrollToPosition(newMsgPosition);
                    inputTxt.setText("");
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("fromLoad")) {
            Intent intent = new Intent(this, Main2Activity.class);
            this.startActivity(intent);
            IntentMappingSharredPrefferences.saveTheIntentMap(IntentMappingSharredPrefferences.NO_FLAG_TO_COMMENTS_STRING, null, this);
        } else
            super.onBackPressed();
    }

    private void populateWithData() {
        notificationRequest = new NotificationRequest(getIntent().getStringExtra("timestamp"));
        pushNotificationsAsyncTask = new PushNotificationsAsyncTask(notificationRequest,
                imagepic,
                posted,
                title,
                body,
                commentArrayList,
                commentsAdapter,
                rvComments,
                this);
        pushNotificationsAsyncTask.execute();
    }

}
