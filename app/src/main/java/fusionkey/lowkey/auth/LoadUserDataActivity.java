package fusionkey.lowkey.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.pushnotifications.RegisterSNS;

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
        private boolean loadingPhoto;

        public AsyncTaskChecker(WeakReference<Activity> activityWeakReference, String userEmail) {
            this.activityWeakReference = activityWeakReference;
            this.userEmail = userEmail;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new RegisterSNS().registerWithSNS();

            LowKeyApplication.userManager.requestCurrentUserDetails(userEmail, null);

            // Now access the S3 photo with the new user details.
            try {
                loadingPhoto = true;
                final ProfilePhotoUploader profilePhotoUploader =
                        new ProfilePhotoUploader();
                profilePhotoUploader.download(
                        LowKeyApplication.userManager.getPhotoFileName(),
                        new Callback() {
                            @Override
                            public void handle() {
                                loadingPhoto = false;
                                Log.e("success", "successHandler");
                            }
                        },
                        new Callback() {
                            @Override
                            public void handle() {
                                loadingPhoto = false;
                                Log.e("fail", "failHandler");
                            }
                        }
                );
                while (loadingPhoto) ;

                LowKeyApplication.profilePhoto = profilePhotoUploader.getPhoto();
            } catch (NullPointerException e) {
                // Forced try...catch so the app wont crash.
                Log.e("LoadUserDataAc bg task", e.getMessage());
                Toast.makeText(activityWeakReference.get(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(activityWeakReference.get(), Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activityWeakReference.get().startActivity(intent);
        }
    }
}
