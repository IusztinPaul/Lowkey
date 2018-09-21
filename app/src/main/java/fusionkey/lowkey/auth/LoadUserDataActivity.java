package fusionkey.lowkey.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;

public class LoadUserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_user_data);

        new AsyncTaskChecker(new WeakReference<Activity>(this)).execute();
    }

    private static class AsyncTaskChecker extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> activityWeakReference;
        private boolean loadingPhoto;

        public AsyncTaskChecker(WeakReference<Activity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // First wait for the user details which are loaded from the login login.
            while (LowKeyApplication.userManager.getUserDetails() == null) ;

            // Now access the S3 photo with the new user details.
            loadingPhoto = true;
            final ProfilePhotoUploader profilePhotoUploader =
                    new ProfilePhotoUploader();
            profilePhotoUploader.download(
                    /**
                     * @TO-DO la linia asta e eroare
                     *      Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser.getUserId()'
                     *      on a null object reference
                     */
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
            while (loadingPhoto);

            LowKeyApplication.profilePhoto = profilePhotoUploader.getPhoto();
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
