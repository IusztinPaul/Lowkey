package fusionkey.lowkey.pushnotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.entryActivity.EntryActivity;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            handleNow(remoteMessage);
            saveNotification(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Handles the message from the cloud and show it as a notification
     * @param remoteMessage
     */

    private void handleNow(RemoteMessage remoteMessage){
        Intent intent = new Intent(this, EntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String data = remoteMessage.getData().toString();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"fs")
                .setSmallIcon(R.drawable.schat)
                .setContentTitle("My notification")
                .setContentText(data)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());

    }
    private void saveNotification(RemoteMessage remoteMessage){
        Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
        String id = attributes.get(UserAttributesEnum.EMAIL.toString());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        int counter = preferences.getInt(id,0);
        editor.putInt(id,counter+1);
        editor.putString(id+preferences.getInt(id,0),remoteMessage.getData().toString());
        editor.putInt("newnotif",1);
        editor.apply();
    }


}
