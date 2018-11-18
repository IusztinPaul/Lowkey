package fusionkey.lowkey.pushnotifications.notificationsV1.models;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.listAdapters.NotificationAdapters.NotificationAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.interfaces.NewsFeedVolleyCallBack;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTO;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTOBuilder;

@Deprecated
public class LoadNotifPhotosAsync extends AsyncTask<Void, String, JSONObject> {

    private ArrayList<NotificationTO> notificationTOArrayList;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Long> timestampsArrayList = new ArrayList<>();
    private NewsFeedRequest newsFeedRequest;
    String id = LowKeyApplication.userManager.getCachedEmail();
    private int GOD_INT;

    public LoadNotifPhotosAsync(NewsFeedRequest newsFeedRequest, ArrayList<NotificationTO> notificationTOArrayList, NotificationAdapter notificationAdapter, int GOD_INT) {
        this.newsFeedRequest = newsFeedRequest;
        this.notificationTOArrayList = notificationTOArrayList;
        this.notificationAdapter = notificationAdapter;
        this.GOD_INT = GOD_INT;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        /*** Get your timestamps first */
        newsFeedRequest.getYourQuestions(new NewsFeedVolleyCallBack() {
            @Override
            public void onError(String message) {
                Log.e(NewsFeedRequest.RESPONSE_ERROR, message);
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("RESPONSE :", response.toString());

                    JSONArray arr = new JSONArray(response.getString("data"));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        timestampsArrayList.add(obj.getLong("postTStamp"));

                    }
                } catch (JSONException e) {
                    Log.e(NewsFeedRequest.GET_QUESTION_STRING, e.toString());
                }
            }
        });

        /* Now that you have the list, prepare the recyclerView for the total ass FUCK*/
        boolean found = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LowKeyApplication.instance);
        int counter = preferences.getInt(id, 0);

        for (int i = GOD_INT; i < counter; i++) {
            String[] s = preferences.getString(id + i, "").split("muiepsdasdfghjkl");


            for (Long l : timestampsArrayList) {
                if (s[1].equals(String.valueOf(l))) {
                    final NotificationTO notificationTO = new NotificationTOBuilder(
                            s[2].replace("}", ""),
                            s[0].replace("{default=", "") + " answered your question : " + s[3].replace("}", ""),
                            s[1],
                            s[4].replace("}", ""))
                            .build();
                    //loadUserPhoto(notificationTO);
                    notificationTOArrayList.add(notificationTO);
                    found = true;
                    final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                    photoUploader.download(notificationTO.getUserID(),
                            new Callback() {
                                @Override
                                public void handle() {
                                    Log.e("PHOTO", "photo downloaded");
                                    notificationTO.setFile(photoUploader.getFileTO());
                                    notificationAdapter.notifyDataSetChanged();
                                }
                            }, new Callback() {
                                @Override
                                public void handle() {
                                }
                            });
                    notificationTOArrayList.add(notificationTO);
                    break;
                }
            }

            if (!found) {
                final NotificationTO notificationTO = new NotificationTOBuilder(
                        s[2].replace("}", ""),
                        s[0].replace("{default=", "") + " answered a question that you have commented : " + s[3].replace("}", ""),
                        s[1],
                        s[4].replace("}", ""))
                        .build();
                //loadUserPhoto(notificationTO);
                final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                photoUploader.download(notificationTO.getUserID(),
                        new Callback() {
                            @Override
                            public void handle() {
                                Log.e("PHOTO", "photo downloaded");
                                notificationTO.setFile(photoUploader.getFileTO());
                                notificationAdapter.notifyDataSetChanged();
                            }
                        }, new Callback() {
                            @Override
                            public void handle() {
                            }
                        });

                notificationTOArrayList.add(notificationTO);
            }
            found = false;
            publishProgress();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        int newMsgPosition = notificationTOArrayList.size() - 1;
        notificationAdapter.notifyItemInserted(newMsgPosition);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

    }

}