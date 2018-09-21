package fusionkey.lowkey.newsfeed;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.NewsfeedAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;

public class NewsFeedAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private ArrayList<NewsFeedMessage> newsFeedMessageArrayList;
    private WeakReference<RecyclerView> recyclerView;
    private NewsfeedAdapter newsfeedAdapter;
    private NewsfeedRequest newsfeedRequest;

    /**
     * @TODO: Prin parametrul asta m-am gandit sa impartim toata lista in segment de lungimea
     * paginilor care vin din API (this.getNextPageNumber() da lungimea intervalului). Asa oarecum putem sa tinem si evidenta a itemelor care sunt deja
     * si alea care vin noi (am facut eu o implementare mai jos pe logica asta) -> asa nu se mai incarca
     * toate datele la fiecare refresh, eu am lasat doar comment-urile sa se mai reincarce, dar na asta
     * se schimba rapid dupa. Asa ai putea sa creezi un AsyncTask pentru fiecare interval si sa faci
     * ceva computation in paralel pentru fiecare interval (deja la 5>= pagini eu zic ca se merita
     * sa split-ui asa treaba.
     */
    private int page = 0;

    public NewsFeedAsyncTask(ArrayList<NewsFeedMessage> newsFeedMessageArrayList,RecyclerView recyclerView,NewsfeedAdapter newsfeedAdapter,NewsfeedRequest newsfeedRequest){
        this.newsFeedMessageArrayList=newsFeedMessageArrayList;
        this.recyclerView = new WeakReference<>(recyclerView);
        this.newsfeedAdapter = newsfeedAdapter;
        this.newsfeedRequest = newsfeedRequest;
    }

    @Override
    protected void onPreExecute() {
        newsfeedAdapter.clear();
        newsfeedAdapter.notifyDataSetChanged();
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

                        final NewsFeedMessage newsFeedMessage;
                        if(isCached)
                            newsFeedMessage = newsFeedMessageArrayList.get(page+i);
                        else
                            newsFeedMessage = new NewsFeedMessage();

                        // Create post only if it doesn't exists.
                        if(!isCached) {
                            String email = obj.getString("userId");

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
                                            newsFeedMessage.setUserPhoto(photoUploader.getPhoto());
                                            newsfeedAdapter.notifyDataSetChanged();
                                        }
                                    }, null);

                            newsFeedMessage.setWeekDay(obj.getInt("weekDay"));
                            newsFeedMessage.setId(obj.getString("userId"));
                            newsFeedMessage.setContent(obj.getString("postTxt"));
                            newsFeedMessage.setDate(obj.getString("postTStamp"));
                            newsFeedMessage.setTitle(obj.getString("postTitle"));

                            String anon = (obj.getString("isAnonymous"));
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
                                    Log.e("Comment",commentObj.toString());
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
        newsfeedAdapter.notifyDataSetChanged();
        recyclerView.get().scrollToPosition(newMsgPosition);

    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

    }

    private int getNextPageNumber() {
        return page + NewsFeedTab.NEWS_FEED_PAGE_SIZE;
    }
}
