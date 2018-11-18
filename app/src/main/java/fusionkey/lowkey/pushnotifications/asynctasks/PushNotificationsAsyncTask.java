package fusionkey.lowkey.pushnotifications.asynctasks;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.CommentAdapters.CommentAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.activities.CommentsFromNotificationActivity;
import fusionkey.lowkey.pushnotifications.requestUtils.INotificationResponseListener;
import fusionkey.lowkey.pushnotifications.requestUtils.NotificationRequest;

public class PushNotificationsAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private NotificationRequest notificationRequest;
    private String userEmail;
    private NewsFeedMessage newsFeedMessage;
    private WeakReference<CircleImageView> circleImageView;
    private WeakReference<TextView> username;
    private WeakReference<TextView> title;
    private ArrayList<Comment> comments;
    private WeakReference<TextView> body;
    private WeakReference<RecyclerView> recyclerViewWeakReference;
    private CommentAdapter commentAdapter;
    private WeakReference<Context> context;

    public PushNotificationsAsyncTask(NotificationRequest notificationRequest,
                                      CircleImageView circleImageView,
                                      TextView username,
                                      TextView title,
                                      TextView body,
                                      ArrayList<Comment> comments,
                                      CommentAdapter commentAdapter,
                                      RecyclerView recyclerView,
                                      Context context){

        this.circleImageView = new WeakReference<>(circleImageView);
        this.username = new WeakReference<>(username);
        this.title = new WeakReference<>(title);
        this.body = new WeakReference<>(body);
        this.notificationRequest = notificationRequest;
        this.commentAdapter = commentAdapter;
        this.comments = comments;
        this.recyclerViewWeakReference = new WeakReference<>(recyclerView);
        this.userEmail = LowKeyApplication.userManager.getCurrentUserEmail();
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        Log.e("MERE","IN PLM>?");


    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        notificationRequest.getQuestion(new INotificationResponseListener() {
            @Override
            public void onError(String message) {
                Log.e(NotificationRequest.RESPONSE_ERROR, message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("RESPONSE :",response.toString());

                    JSONObject obj = response.getJSONObject("data");

                    newsFeedMessage = new NewsFeedMessage();
                    String email = obj.getString("userId");
                    String anon = obj.getString("isAnonymous");

                    /*** Setting the Photo */
                    newsFeedMessage.setUserPhoto(BitmapFactory.decodeResource(
                            LowKeyApplication.instance.getResources(),
                            R.drawable.avatar_placeholder)
                    );
                    final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                    photoUploader.download(UserManager.parseEmailToPhotoFileName(email),
                            new Callback() {
                                @Override
                                public void handle() {
                                    Log.e("PHOTO", "photo downloaded");
                                    Picasso.with(context.get()).load(photoUploader.getFileTO()).into(circleImageView.get());
                                }
                            }, null);


                    UserAttributeManager userAttributeManager = new UserAttributeManager(email);
                    newsFeedMessage.setWeekDay(obj.getInt("weekDay"));
                    newsFeedMessage.setId(obj.getString("userId"));
                    newsFeedMessage.setContent(obj.getString("postTxt"));
                    newsFeedMessage.setTimeStamp(obj.getLong("postTStamp"));
                    newsFeedMessage.setTitle(obj.getString("postTitle"));
                    newsFeedMessage.setSNStopic(obj.getString("snsTopic"));
                    newsFeedMessage.setUser(userAttributeManager.getUsername());
                    if (newsFeedMessage.getId().equals(userEmail))
                        newsFeedMessage.setType(NewsFeedMessage.NORMAL);
                    else
                        newsFeedMessage.setType(NewsFeedMessage.OTHER_QUESTIONS);
                    if (anon.equalsIgnoreCase("true") || anon.equalsIgnoreCase("true"))
                        newsFeedMessage.setAnon(Boolean.valueOf(anon));
                    else
                        newsFeedMessage.setAnon(false);


                    try {
                        JSONArray arr2 = new JSONArray(obj.getString("comments")); //get comments
                        for (int j = 0; j < arr2.length(); j++) {
                            JSONObject comment = arr2.getJSONObject(j);
                            Comment commentObj = new Comment(
                                    comment.getString("commentIsAnonymous"),
                                    comment.getString("commentTStamp"),
                                    "MODIFY DYNAMO TABLE",
                                    //comment.getString("commentUserUsername"),
                                    comment.getString("commentTxt"),
                                    comment.getString("commentUserId"));
                            comments.add(commentObj);
                        }
                    } catch (JSONException e) {
                        Log.e("Comments", "The post has no comments");
                    }
                    newsFeedMessage.setCommentArrayList(comments);
                    publishProgress();

                }catch(JSONException e){
                    Log.e(NewsFeedRequest.GET_QUESTION_STRING, e.toString());
                }
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        title.get().setText(newsFeedMessage.getTitle());
        body.get().setText(newsFeedMessage.getContent());
        username.get().setText(newsFeedMessage.getUser());
        commentAdapter.setCommentList(comments);
        commentAdapter.notifyDataSetChanged();
        int newMsgPosition = comments.size() - 1;
        recyclerViewWeakReference.get().scrollToPosition(newMsgPosition);
        CommentsFromNotificationActivity.snsTOPIC = newsFeedMessage.getSNStopic();

    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.e("GetQfromTS:","Succes !");






    }

}