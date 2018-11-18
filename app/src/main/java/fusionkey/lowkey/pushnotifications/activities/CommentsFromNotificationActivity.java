package fusionkey.lowkey.pushnotifications.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.listAdapters.CommentAdapters.CommentAdapter;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.asynctasks.PushNotificationsAsyncTask;
import fusionkey.lowkey.pushnotifications.requestUtils.NotificationRequest;
import fusionkey.lowkey.pushnotifications.service.IntentMappingSharredPrefferences;

public class CommentsFromNotificationActivity extends AppCompatActivity {

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

    private CommentAdapter commentsAdapter = new CommentAdapter(commentArrayList,this);
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
                String commentText = inputTxt.getText().toString().trim();

                if(!TextUtils.isEmpty(commentText)) {
                    Comment comment = createComment(commentText);
                    saveComment(comment);
                    adaptView();
                }
            }
        });

    }

    private Comment createComment(String commentText) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UserDB attributes = LowKeyApplication.userManager.getUserDetails();
        final String userEmail = attributes.getUserEmail();
        final String username = attributes.getUsername();

        return new Comment(
                "true",
                timestamp + "",
                commentText,
                userEmail,
                username
        );
    }

    private void saveComment(Comment comment) {
        new NewsFeedRequest(comment.getCommentUserUsername()).
                postComment(comment, snsTOPIC, LowKeyApplication.endpointArn);
        commentArrayList.add(comment);
    }

    private void adaptView() {
        int newMsgPosition = commentArrayList.size() - 1;
        commentsAdapter.notifyItemInserted(newMsgPosition);
        commentsAdapter.notifyDataSetChanged();
        rvComments.scrollToPosition(newMsgPosition);
        inputTxt.setText("");
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
