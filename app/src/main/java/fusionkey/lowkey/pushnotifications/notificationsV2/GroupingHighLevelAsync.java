package fusionkey.lowkey.pushnotifications.notificationsV2;

import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.listAdapters.NotificationAdapters.NotificationAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.GroupNotificationAbstract;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.Notification;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.NotificationO;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.NotificationY;


public class GroupingHighLevelAsync extends AsyncTask<Void, String, JSONObject> {

    private NotificationAdapter notificationAdapter;
    private static HashMap<String, ArrayList<Notification>> highLevelHashMap = new HashMap<>();
    private static List<Long> timestamps = LowKeyApplication.userManager.getUserDetails().getTimeStamps();
    private ArrayList<GroupNotificationAbstract> notificationAbstracts;

    public GroupingHighLevelAsync(NotificationAdapter notificationAdapter, ArrayList<GroupNotificationAbstract> notificationAbstracts) {
        this.notificationAdapter = notificationAdapter;
        this.notificationAbstracts = notificationAbstracts;
    }

    @Override
    protected void onPreExecute() {
        highLevelHashMap = GroupingLowLevel.getHashMap();

    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        /* Grouping the notifications */
        for (Long l : timestamps)
            if (highLevelHashMap.containsKey(String.valueOf(l))) {
                int size = highLevelHashMap.get(String.valueOf(l)).size() - 1;
                Notification notification = highLevelHashMap.get(String.valueOf(l)).get(size);
                final NotificationY notificationY = new NotificationY(notification.getTimestamp(), size, notification);
                highLevelHashMap.remove(String.valueOf(l));
                final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
                photoUploader.download(notification.getUserID(),
                        new Callback() {
                            @Override
                            public void handle() {
                                Log.e("PHOTO", "photo downloaded");
                                notificationY.setFile(photoUploader.getFileTO());
                                notificationAbstracts.add(notificationY);
                                publishProgress();
                            }
                        }, new Callback() {
                            @Override
                            public void handle() {
                            }
                        });

            }
        for (Map.Entry<String, ArrayList<Notification>> map : highLevelHashMap.entrySet()) {
            int size = map.getValue().size() - 1;
            Notification notification = map.getValue().get(size);
            final NotificationO notificationO = new NotificationO(notification.getTimestamp(), size, notification);
            final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
            photoUploader.download(notification.getUserID(),
                    new Callback() {
                        @Override
                        public void handle() {
                            Log.e("PHOTO", "photo downloaded");
                            notificationO.setFile(photoUploader.getFileTO());
                            notificationAbstracts.add(notificationO);
                            publishProgress();
                        }
                    }, new Callback() {
                        @Override
                        public void handle() {
                        }
                    });

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        notificationAdapter.notifyItemInserted(notificationAbstracts.size()-1);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        notificationAdapter.setLoaded();
    }

}