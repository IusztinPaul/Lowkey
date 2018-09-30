package fusionkey.lowkey.newsfeed.asynctasks;

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

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;

public class GetYourQuestionsAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private ArrayList<NewsFeedMessage> newsFeedMessageArrayList;
    private WeakReference<RecyclerView> recyclerView;
    private NewsFeedAdapter newsFeedAdapter;
    private NewsFeedRequest newsFeedRequest;
    private List<UserType> userTypeList = LowKeyApplication.userManager.getUsers(UserAttributesEnum.EMAIL, null);



    public GetYourQuestionsAsyncTask(ArrayList<NewsFeedMessage> newsFeedMessageArrayList, RecyclerView recyclerView, NewsFeedAdapter newsFeedAdapter, NewsFeedRequest newsFeedRequest){
        this.newsFeedMessageArrayList=newsFeedMessageArrayList;
        this.recyclerView = new WeakReference<>(recyclerView);
        this.newsFeedAdapter = newsFeedAdapter;
        this.newsFeedRequest = newsFeedRequest;
    }

    @Override
    protected void onPreExecute() {

       // newsfeedAdapter.clear();
       // newsfeedAdapter.notifyDataSetChanged();


    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        newsFeedRequest.getYourQuestions(new NewsFeedVolleyCallBack() {
            @Override
            public void onError(String message) {
                Log.e(NewsFeedRequest.RESPONSE_ERROR, message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("RESPONSE :",response.toString());

                    JSONArray arr = new JSONArray(response.getString("data"));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        NewsFeedMessage newsFeedMessage = new NewsFeedMessage();
                        newsFeedMessage.setWeekDay(obj.getInt("weekDay"));
                        newsFeedMessage.setId(obj.getString("userId"));
                        newsFeedMessage.setContent(obj.getString("postTxt"));newsFeedMessage.setTimeStamp(obj.getLong("postTStamp"));
                        newsFeedMessage.setTitle(obj.getString("postTitle"));
                        newsFeedMessage.setType(NewsFeedMessage.NORMAL);
                        newsFeedMessage.setUser(getUsername(obj.getString("userId")));
                        String anon = (obj.getString("isAnonymous"));
                        if(anon.equalsIgnoreCase("true")|| anon.equalsIgnoreCase("true"))
                            newsFeedMessage.setAnon(Boolean.valueOf(anon));
                        else
                            newsFeedMessage.setAnon(false);

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
                        newsFeedMessageArrayList.add(newsFeedMessage);
                        publishProgress();

                    }
                }catch(JSONException e){
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
        newsFeedAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
       // newsfeedAdapter.notifyItemInserted(0);
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
}