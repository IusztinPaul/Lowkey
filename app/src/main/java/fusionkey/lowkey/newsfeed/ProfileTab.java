package fusionkey.lowkey.newsfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.R;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;



public class ProfileTab extends Fragment {

    TextView username;
    CircleImageView circleImageView;
    ImageView imageViewEdit;
    ImageView settings;
    SharedPreferences preferences;
    TabLayout tabLayout;
    ViewPager pager;

    private static String spKey = "newnotif";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_reload, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        imageViewEdit = rootView.findViewById(R.id.imageViewEdit);
        username = rootView.findViewById(R.id.username2);
        circleImageView = rootView.findViewById(R.id.circleImageView3);
        settings = rootView.findViewById(R.id.settingsImg);

        pager=rootView.findViewById(R.id.pager);
        tabLayout =rootView.findViewById(R.id.tabDots);
        pager.setAdapter(buildAdapter());
        tabLayout.setupWithViewPager(pager, true);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("token : ", FirebaseInstanceId.getInstance().getToken());
                getActivity().startActivity(new Intent(getContext().getApplicationContext(), fusionkey.lowkey.main.menu.Menu.class));
            }
        });

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewEdit.setBackgroundResource(R.drawable.nonotif);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(spKey,0);
                editor.apply();
                pager.setCurrentItem(2);
            }
        });


        loadUserPhoto();
        setupNotifications();
        populateUI();

        return rootView;
    }

    @Override
    public void onResume(){
        populateUI();
        Log.e("onResumeProfile", "called");
        super.onResume();
    }

    @Override
    public void onStart(){
        populateUI();
        super.onStart();
    }

    private PagerAdapter buildAdapter() {
        return(new ProfileSampleAdapter(getActivity(),getChildFragmentManager()));
    }

    private void setupNotifications(){
        if(preferences.getInt("newnotif",0)==1){
            imageViewEdit.setBackgroundResource(R.drawable.notif);
        }else
            imageViewEdit.setBackgroundResource(R.drawable.nonotif);
    }

    public void populateUI(){
        try {
            UserDB attributes = LowKeyApplication.userManager.getUserDetails();
            String usernameS = attributes.getUsername();
            username.setText(usernameS != null ? usernameS : "");

        } catch (NullPointerException e) {
            Log.e("NullPointerExp", "User details not loaded yet");
        }
    }

    private void loadUserPhoto(){
        final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
        photoUploader.download(UserManager.parseEmailToPhotoFileName(LowKeyApplication.userManager.getParsedUserEmail()),
                new Callback() {
                    @Override
                    public void handle() {
                        Log.e("PHOTO", "photo downloaded");
                        Picasso.with(getContext()).load(photoUploader.getFileTO()).into(circleImageView);
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
