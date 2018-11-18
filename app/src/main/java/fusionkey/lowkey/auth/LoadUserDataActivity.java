package fusionkey.lowkey.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.asynctasks.GetYourTimestampsAsyncTask;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.activities.CommentsFromNotificationActivity;
import fusionkey.lowkey.pushnotifications.service.IntentMappingSharredPrefferences;
import fusionkey.lowkey.pushnotifications.service.RegisterSNS;

public class LoadUserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_user_data);

        String userEmail = this.getIntent().getStringExtra(UserAttributesEnum.EMAIL.toString());


        new AsyncTaskChecker(new WeakReference<Activity>(this), userEmail).execute();
    }

    private static class AsyncTaskChecker extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> activityWeakReference;
        private String userEmail;

        public AsyncTaskChecker(WeakReference<Activity> activityWeakReference, String userEmail) {
            this.activityWeakReference = activityWeakReference;
            this.userEmail = userEmail;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new RegisterSNS().registerWithSNS();
            LowKeyApplication.userManager.requestCurrentUserDetails(userEmail, null);
            new GetYourTimestampsAsyncTask(new NewsFeedRequest(userEmail)).execute();
            loadUserPhoto();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(IntentMappingSharredPrefferences.getTheIntentFlag(activityWeakReference.get().getApplicationContext()).equals(IntentMappingSharredPrefferences.FLAG_TO_COMMENTS_STRING)){
                Intent intent = new Intent(activityWeakReference.get(), CommentsFromNotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("timestamp",IntentMappingSharredPrefferences.getTheIntentTimestamp(activityWeakReference.get().getApplicationContext()));
                intent.putExtra("from","fromLoad");
                activityWeakReference.get().startActivity(intent);
            }else {
                Intent intent = new Intent(activityWeakReference.get(), Main2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityWeakReference.get().startActivity(intent);
            }
        }

        private void loadUserPhoto() {
            final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
            photoUploader.download(UserManager.parseEmailToPhotoFileName(LowKeyApplication.userManager.getCachedEmail()),
                    new Callback() {
                        @Override
                        public void handle() {
                            Log.e("PHOTO", "photo downloaded");
                            LowKeyApplication.userManager.profilePhoto =  photoUploader.getPhoto();
                            LowKeyApplication.userManager.photoFile = photoUploader.getFileTO();
                        }
                    }, new Callback() {
                        @Override
                        public void handle() {
                        }
                    });
        }
    }
}
