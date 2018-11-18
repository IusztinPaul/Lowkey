package fusionkey.lowkey.newsfeed.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

public class GetYourTimestampsAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private NewsFeedRequest newsFeedRequest;
    private ArrayList<Long> timestampsArrayList = new ArrayList<>();

    public GetYourTimestampsAsyncTask(NewsFeedRequest newsFeedRequest){
        this.newsFeedRequest = newsFeedRequest;
    }

    @Override
    protected void onPreExecute() {

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
                        timestampsArrayList.add(obj.getLong("postTStamp"));
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

    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        UserDB userDB = LowKeyApplication.userManager.getUserDetails();
        userDB.setTimeStamps(timestampsArrayList);
        LowKeyApplication.userManager.updateCurrentUser(userDB);
    }

}
