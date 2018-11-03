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
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
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
        private boolean loadingPhoto;

        public AsyncTaskChecker(WeakReference<Activity> activityWeakReference, String userEmail) {
            this.activityWeakReference = activityWeakReference;
            this.userEmail = userEmail;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new RegisterSNS().registerWithSNS();
            LowKeyApplication.userManager.requestCurrentUserDetails(userEmail, null);
            loadUserPhoto();
            /**
             * Mai bine renuntam sa facem load aici, Picasso oricum face asta destul de bine si putem face
             * direct din Pagina de profil .
             */

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(activityWeakReference.get(), Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activityWeakReference.get().startActivity(intent);
        }

        private void loadUserPhoto(){
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
