package fusionkey.lowkey.main;

import android.arch.lifecycle.LifecycleObserver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amazonaws.mobile.client.AWSMobileClient;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.auth.utils.UserDBManager;
import fusionkey.lowkey.models.UserDB;

public class Main2Activity extends AppCompatActivity implements LifecycleObserver,MainCallback {
    private LoadingAsyncTask loadingAsyncTask;
    public static String currentUser = getParsedEmail();

    static public boolean SEARCH_STATE;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private CardView searchCard;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(0);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        progressBar = (ProgressBar) findViewById(R.id.paymentBar);
        searchCard = (CardView) findViewById(R.id.searchCard);
        imageView = findViewById(R.id.imageView8);

        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(this).execute();

        UserDBManager.create("Andrei");
        UserDBManager.update("Paul", 20);
        UserDBManager.update("Sebi", 50);
        Log.e("USEEEER", UserDBManager.getUserData("Paul").toString());
        UserDBManager.delete("Andrei");
    }
    @Override
    public void searchForHelp() {
        loadingAsyncTask = new LoadingAsyncTask(currentUser, this, progressBar, true, searchCard);
        loadingAsyncTask.execute();
        saveState("step", 0);
    }
    @Override
    public void helpOthers() {

        searchCard.setVisibility(View.VISIBLE);
        loadingAsyncTask = new LoadingAsyncTask(currentUser, this, progressBar, false, searchCard);
        loadingAsyncTask.execute();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingAsyncTask.cancel(true);
                saveState("step", 0);
                doNothing();
            }
        });
    }

    private void doNothing() {
        searchCard.setVisibility(View.INVISIBLE);
    }

    private void saveState(String key, int step) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, step);
        editor.apply();
    }

    private int loadState() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getInt("step", 0);
    }


    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ProfileTab Chat = new ProfileTab();
                    return Chat;
                case 1:
                    NewsFeedTab Contacts = new NewsFeedTab();
                    return Contacts;
                case 2:
                    StartTab Profile = new StartTab();
                    return Profile;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chat";
                case 1:
                    return "Contacts";
                case 2:
                    return "Profile";

            }
            return null;
        }
    }

    private static String getParsedEmail() {
        String email = LowKeyApplication.userManager.getUserDetails().getAttributes().
                getAttributes().get(UserAttributesEnum.EMAIL.toString());

        return email.replace("@", "").replace(".", "");
    }

    @Override
    protected void onPause() {
        Log.e("onPauseMain2Activity", "called");
        // Close queue logic.
        if (loadingAsyncTask != null) {
            saveState("step",0);
            loadingAsyncTask.cancel(true);
            searchCard.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

}
