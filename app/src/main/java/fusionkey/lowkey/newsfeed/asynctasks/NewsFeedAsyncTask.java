package fusionkey.lowkey.newsfeed.asynctasks;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;

import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;

import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;

public class NewsFeedAsyncTask extends AsyncTask<Void, String, JSONObject> {

    private ArrayList<NewsFeedMessage> newsFeedMessageArrayList;
    private WeakReference<RecyclerView> recyclerView;
    private NewsFeedAdapter newsFeedAdapter;
    private NewsFeedRequest newsFeedRequest;
    private String userEmail;

    private Long referenceTimestamp;
    private IGenericConsumer<Long> setter;
    private boolean isNew;
    private boolean isStart;

    public NewsFeedAsyncTask(ArrayList<NewsFeedMessage> newsFeedMessageArrayList,
                             RecyclerView recyclerView,
                             NewsFeedAdapter newsFeedAdapter,
                             NewsFeedRequest newsFeedRequest,
                             Long referenceTimestamp,
                             IGenericConsumer<Long> setter,
                             boolean isNew,
                             boolean isStart) {
        this.newsFeedMessageArrayList = newsFeedMessageArrayList;
        this.recyclerView = new WeakReference<>(recyclerView);
        this.newsFeedAdapter = newsFeedAdapter;
        this.newsFeedRequest = newsFeedRequest;
        this.referenceTimestamp = referenceTimestamp;
        this.setter = setter;
        this.userEmail = LowKeyApplication.userManager.getCurrentUserEmail();
        this.isNew = isNew;
        this.isStart = isStart;
    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        newsFeedRequest.getNewsFeed(referenceTimestamp, isStart, new NewsFeedVolleyCallBack() {
            @Override
            public void onError(String message) {
                Log.e(NewsFeedRequest.RESPONSE_ERROR, message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int cachedIndex = -1;

                    JSONArray arr = new JSONArray(response.getString("data"));

                    // Try to find the items in the existing array list.
                    if (!isNew && isStart && arr.length() > 0) {
                        cachedIndex = newsFeedMessageArrayList.indexOf(new NewsFeedMessage(referenceTimestamp));
                    } else if(!isNew && !isStart && arr.length() > 0) {
                        long timestamp = arr.getJSONObject(0).getLong("postTStamp");
                        cachedIndex = newsFeedMessageArrayList.indexOf(new NewsFeedMessage(timestamp));
                    }

                    if(setter != null) {
                        if(arr.length() > 0)
                            setter.consume(arr.getJSONObject(arr.length() - 1).getLong("postTStamp"));
                        else
                            setter.consume(null);
                    }

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        if (obj == null)
                            Log.e("don't add", "!!");

                        String email = obj.getString("userId");

                        final NewsFeedMessage newsFeedMessage;
                        if (cachedIndex != -1)
                            newsFeedMessage = newsFeedMessageArrayList.get(cachedIndex + i);
                        else
                            newsFeedMessage = new NewsFeedMessage();

                        // Update post only if it doesn't exists.
                        if (cachedIndex == -1) {
                            String anon = obj.getString("isAnonymous");

                            // Set photo logic.
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
                                            newsFeedMessage.setFile(photoUploader.getFileTO());
                                            newsFeedAdapter.notifyDataSetChanged();
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
                            /*
                            final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                            photoUploader.download(UserManager.parseEmailToPhotoFileName(m1.getId()),
                                    new Callback() {
                                        @Override
                                        public void handle() {
                                            Log.e("PHOTO", "photo downloaded");
                                            m1.setFile(photoUploader.getFileTO());
                                            //the newsfeedmessage it's added when the downloading it's finished and the file it's exist
                                            messages.add(m1);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }, null);
                            */
                            newsFeedMessageArrayList.add(newsFeedMessage);

                        }

                        // Refresh comments in any case.
                        ArrayList<Comment> commentArrayList = new ArrayList<>();

                        try {
                            JSONArray arr2 = new JSONArray(obj.getString("comments")); //get comments
                            for (int j = 0; j < arr2.length(); j++) {
                                JSONObject comment = arr2.getJSONObject(j);
                                Comment commentObj = new Comment(
                                        comment.getString("commentIsAnonymous"),
                                        comment.getString("commentTStamp"),
                                        comment.getString("commentTxt"),
                                        comment.getString("commentUserId"));
                                commentArrayList.add(commentObj);
                            }
                        } catch (JSONException e) {
                            Log.e("Comments", "The post has no comments");
                        }
                        newsFeedMessage.setCommentArrayList(commentArrayList);

                        publishProgress();
                    }

                } catch (JSONException e) {
                    Log.e(NewsFeedRequest.GET_QUESTION_STRING, e.toString());
                }
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        int newMsgPosition = newsFeedMessageArrayList.size() - 1;
        newsFeedAdapter.notifyItemInserted(newMsgPosition);


    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        newsFeedAdapter.setLoaded();
    }
}
