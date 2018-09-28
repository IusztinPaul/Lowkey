package fusionkey.lowkey.newsfeed.asynctasks;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;

import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;

import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.NewsfeedAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.NewsFeedTab;
import fusionkey.lowkey.newsfeed.util.NewsfeedRequest;
import fusionkey.lowkey.newsfeed.interfaces.NewsfeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;

public class NewsFeedAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private ArrayList<NewsFeedMessage> newsFeedMessageArrayList;
    private WeakReference<RecyclerView> recyclerView;
    private NewsfeedAdapter newsfeedAdapter;
    private NewsfeedRequest newsfeedRequest;
    private List<UserType> userTypeList = LowKeyApplication.userManager.getUsers(UserAttributesEnum.EMAIL, null);
    Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
    final String uniqueId = attributes.get(UserAttributesEnum.EMAIL.toString());


    /**
     * @TODO: Prin parametrul asta m-am gandit sa impartim toata lista in segment de lungimea
     * paginilor care vin din API (this.getNextPageNumber() da lungimea intervalului). Asa oarecum putem sa tinem si evidenta a itemelor care sunt deja
     * si alea care vin noi (am facut eu o implementare mai jos pe logica asta) -> asa nu se mai incarca
     * toate datele la fiecare refresh, eu am lasat doar comment-urile sa se mai reincarce, dar na asta
     * se schimba rapid dupa. Asa ai putea sa creezi un AsyncTask pentru fiecare interval si sa faci
     * ceva computation in paralel pentru fiecare interval (deja la 5>= pagini eu zic ca se merita
     * sa split-ui asa treaba.
     */
    private int page;

    public NewsFeedAsyncTask(ArrayList<NewsFeedMessage> newsFeedMessageArrayList,RecyclerView recyclerView,NewsfeedAdapter newsfeedAdapter,NewsfeedRequest newsfeedRequest,int page){
        this.newsFeedMessageArrayList=newsFeedMessageArrayList;
        this.recyclerView = new WeakReference<>(recyclerView);
        this.newsfeedAdapter = newsfeedAdapter;
        this.newsfeedRequest = newsfeedRequest;
        this.page=page;
    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        newsfeedRequest.getNewsfeed(page, new NewsfeedVolleyCallBack() {
            @Override
            public void onError(String message) {
                    Log.e(NewsfeedRequest.RESPONSE_ERROR, message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("RESPONSE :",response.toString());
                    // If the page is smaller than the actual size of the list it means that the
                    // current set of items already exists -> it's cached.
                    boolean isCached = true;
                    if (page >= newsFeedMessageArrayList.size())
                        isCached = false;

                    JSONArray arr = new JSONArray(response.getString("data"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String email = obj.getString("userId");
                            final NewsFeedMessage newsFeedMessage;
                             if(isCached)
                                newsFeedMessage = newsFeedMessageArrayList.get(page);
                             else
                            newsFeedMessage = new NewsFeedMessage();

                            // Create post only if it doesn't exists.
                            if(!isCached) {

                            String anon = obj.getString("isAnonymous");
                            // Set photo logic.
                            /**
                             * @TODO Resize the Photos
                             *
                             * +Out of memory
                             */
                            newsFeedMessage.setUserPhoto(BitmapFactory.decodeResource(
                            LowKeyApplication.instance.getResources(),
                            R.drawable.avatar_placeholder)
                            );
                            final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                            photoUploader.download(UserManager.parseEmailToPhotoFileName(email),
                            new Callback() {
                            @Override public void handle() {
                            Log.e("PHOTO", "photo downloaded");
                            newsFeedMessage.setUserPhoto(photoUploader.getPhoto());
                            newsfeedAdapter.notifyDataSetChanged();
                            }
                            }, null);

                            newsFeedMessage.setWeekDay(obj.getInt("weekDay"));
                            newsFeedMessage.setId(obj.getString("userId"));
                            newsFeedMessage.setContent(obj.getString("postTxt"));
                            newsFeedMessage.setDate(obj.getString("postTStamp"));
                            newsFeedMessage.setTitle(obj.getString("postTitle"));
                            newsFeedMessage.setUser(getUsername(obj.getString("userId")));
                            if(newsFeedMessage.getId().equals(uniqueId))
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
                        }catch (JSONException e){
                            Log.e("Comments","The post has no comments");
                        }
                        newsFeedMessage.setCommentArrayList(commentArrayList);

                        // Add it no the array list only if it doesn't exists.
                        if(!isCached)


                        publishProgress();

                    }
                }catch(JSONException e){
                    Log.e(NewsfeedRequest.GET_QUESTION_STRING, e.toString());
                }
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        int newMsgPosition = newsFeedMessageArrayList.size() - 1;
        newsfeedAdapter.notifyItemInserted(newMsgPosition);


    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        newsfeedAdapter.setLoaded();

    }

    private String getUsername(String id) {
        for (UserType e : userTypeList) {
            List<AttributeType> attributeTypeList = e.getAttributes();
            for(AttributeType a : attributeTypeList){
                if(a.getValue().equals(id)){
                    for(AttributeType b : attributeTypeList)
                        if(b.getName().equals("nickname"))
                            return b.getValue();
                }}
        }
        return "User not found";
    }

    private int getNextPageNumber() {
        return page + NewsFeedTab.NEWS_FEED_PAGE_SIZE;
    }
}
