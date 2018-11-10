package fusionkey.lowkey.newsfeed.asynctasks;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;

import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributeManager;

import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;

import static fusionkey.lowkey.newsfeed.models.NewsFeedMessage.KEY_SNS_TOPIC;

public class NewsFeedAsyncTask extends AsyncTask<Void, String, JSONObject> {

    private ArrayList<NewsFeedMessage> newsFeedMessageArrayList;
    private NewsFeedAdapter newsFeedAdapter;
    private NewsFeedRequest newsFeedRequest;
    private String userEmail;

    private Long referenceTimestamp;
    private IGenericConsumer<Long> setter;
    private boolean isNew;
    private boolean isStart;

    public NewsFeedAsyncTask(ArrayList<NewsFeedMessage> newsFeedMessageArrayList,
                             NewsFeedAdapter newsFeedAdapter,
                             NewsFeedRequest newsFeedRequest,
                             Long referenceTimestamp,
                             IGenericConsumer<Long> setter,
                             boolean isNew,
                             boolean isStart) {
        this.newsFeedMessageArrayList = newsFeedMessageArrayList;
        this.newsFeedAdapter = newsFeedAdapter;
        this.newsFeedRequest = newsFeedRequest;
        this.referenceTimestamp = referenceTimestamp;
        this.setter = setter;
        this.userEmail = LowKeyApplication.userManager.getCurrentUserEmail();
        this.isNew = isNew;
        this.isStart = isStart;
    }

    @Override
    protected void onPreExecute() { }

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
                            newsFeedMessage.setSNStopic(obj.getString(KEY_SNS_TOPIC));
                            newsFeedMessage.setUser(userAttributeManager.getUsername());
                            if (newsFeedMessage.getId().equals(userEmail))
                                newsFeedMessage.setType(NewsFeedMessage.NORMAL);
                            else
                                newsFeedMessage.setType(NewsFeedMessage.OTHER_QUESTIONS);
                            if (anon.equalsIgnoreCase("true") || anon.equalsIgnoreCase("true"))
                                newsFeedMessage.setAnon(Boolean.valueOf(anon));
                            else
                                newsFeedMessage.setAnon(false);

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

                    // Let's call it last to handle some callbacks in it.
                    if(setter != null) {
                        if(arr.length() > 0) {
                            Long lastPostTStamp = arr.getJSONObject(arr.length() - 1).getLong("postTStamp");
                            setter.consume(lastPostTStamp);
                        }
                        else
                            setter.consume(null);
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
        newsFeedAdapter.notifyDataSetChanged();
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
