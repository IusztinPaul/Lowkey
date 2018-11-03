package fusionkey.lowkey.main;

import android.arch.lifecycle.LifecycleObserver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.MobileAds;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;

import fusionkey.lowkey.main.utils.PhotoUploader;
import fusionkey.lowkey.newsfeed.NewsFeedTab;
import fusionkey.lowkey.newsfeed.ProfileTab;


public class Main2Activity extends AppCompatActivity implements LifecycleObserver,MainCallback {
    private LoadingAsyncTask loadingAsyncTask;
    public static String currentUserParsedEmail = LowKeyApplication.userManager.getParsedUserEmail();
   // MobileAds.Initialize(this, "ca-app-pub-3205268820160972~9635375425");
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
    private InterstitialAd mInterstitialAd;
    private ViewPager mViewPager;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private CardView searchCard;
    private ImageView imageView;
    private FloatingActionButton fab;
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
        mViewPager.setCurrentItem(1);
        progressBar = (ProgressBar) findViewById(R.id.paymentBar);
        searchCard = (CardView) findViewById(R.id.searchCard);
        imageView = findViewById(R.id.imageView8);

        // Admob init
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");



        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(this).execute();


    }
    @Override
    public void searchForHelp() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mInterstitialAd.show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    loadingAsyncTask = new LoadingAsyncTask(Main2Activity.this, progressBar, true, searchCard);
                    loadingAsyncTask.execute();
                    saveState("step", 0);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                    loadingAsyncTask = new LoadingAsyncTask(Main2Activity.this, progressBar, true, searchCard);
                    loadingAsyncTask.execute();
                    saveState("step", 0);
                }
            });

    }
    @Override
    public void helpOthers() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                searchCard.setVisibility(View.VISIBLE);
                loadingAsyncTask = new LoadingAsyncTask(Main2Activity.this, progressBar, false, searchCard);
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

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                searchCard.setVisibility(View.VISIBLE);
                loadingAsyncTask = new LoadingAsyncTask(Main2Activity.this, progressBar, false, searchCard);
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

    @Override
    protected void onPause() {
        Log.e("onPauseMain2Activity", "called");
        // Close queue logic.
        if (loadingAsyncTask != null) {
            saveState("step",0);
            loadingAsyncTask.cancel(true);
            searchCard.setVisibility(View.INVISIBLE);
        }
        PhotoUploader.deleteFolder();
        super.onPause();
    }

}
