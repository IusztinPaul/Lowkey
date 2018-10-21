package fusionkey.lowkey.newsfeed;

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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.CommentAdapters.CommentAdapter;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

public class CommentsActivity extends AppCompatActivity {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    TextView body;
    TextView posted;
    TextView title;
    CoordinatorLayout contentRoot;
    RecyclerView rvComments;
    Button button;
    EditText inputTxt;
    LinearLayout llAddComment;
    NewsFeedCallBack newsFeedCallBack;
    CardView questionInfo;
    List<Comment> commentArrayList;
    ArrayList<Comment> commentsSentList = new ArrayList<>();
    private CommentAdapter commentsAdapter;
    private int drawingStartLocation;


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
        posted = findViewById(R.id.posted);

        inputTxt = findViewById(R.id.chat_input_msg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
        populateWithData();
        setupComments();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(inputTxt.getText().toString())) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
                    final String uniqueID = attributes.get(UserAttributesEnum.EMAIL.toString());
                    final String username = attributes.get(UserAttributesEnum.USERNAME.toString());
                    new NewsFeedRequest(username).postComment(getIntent().getLongExtra("timestampID",0), true, inputTxt.getText().toString(),getIntent().getStringExtra("SNStopic"));
                    commentArrayList.add(new Comment("true",String.valueOf(timestamp.getTime()),inputTxt.getText().toString(),username));
                    commentsSentList.add(new Comment("true",String.valueOf(timestamp.getTime()),inputTxt.getText().toString(),username));

                    int newMsgPosition = commentArrayList.size() - 1;
                    commentsAdapter.notifyItemInserted(newMsgPosition);
                    rvComments.scrollToPosition(newMsgPosition);
                    inputTxt.setText("");
                }

            }
        });

    }





    private void populateWithData(){

        Bundle b = getIntent().getExtras();

        try {
            MyParcelable object = b.getParcelable("parcel");
            commentArrayList = object.getArrList();
            commentsAdapter = new CommentAdapter(commentArrayList,this);
            title.setText(getIntent().getStringExtra("title"));
            body.setText(getIntent().getStringExtra("body"));
            String aux = "posted by " + getIntent().getStringExtra("username");
            posted.setText(aux);
            rvComments.setAdapter(commentsAdapter);
        }catch(NullPointerException e){
            Log.e("Error","parcelable object failed");
        }
    }



    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(100);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {

        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }
    private void setupComments() {
        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent retrieveData = new Intent();
        MyParcelable object = new MyParcelable();
        object.setArrList(commentsSentList);
        retrieveData.putExtra("NewComments", object);
        retrieveData.putExtra("ItemID",getIntent().getLongExtra("timestampID",0));
        setResult(Activity.RESULT_OK,retrieveData);


        contentRoot.animate()
                .translationY(Resources.getSystem().getDisplayMetrics().heightPixels)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        overridePendingTransition(0, 0);

                    }
                })
                .start();

        super.onBackPressed();
        this.finish();
    }

}


