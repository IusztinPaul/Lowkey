package fusionkey.lowkey.main;

import android.content.Intent;
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
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;

public class Main2Activity extends AppCompatActivity {
    public static final String currentUser = "SebastianDevfsdfs";
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
    ProgressBar progressBar;
    CardView searchCard;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Determine if this is first start - and whether to show app intro
        // Determine if the user is logged in

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
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


        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        searchCard = (CardView) findViewById(R.id.searchCard);
        imageView = findViewById(R.id.imageView8);
        Intent intent = getIntent();
        Boolean listenerState = intent.getBooleanExtra("Listener",true);
        String mapping = intent.getStringExtra("Mapping");
        if(mapping != null && listenerState != null) {
            if (mapping.equals("ON") && !listenerState) {
                searchCard.setVisibility(View.VISIBLE);
                final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser, this, progressBar, false);
                loadingAsyncTask.execute();
                SEARCH_STATE=true;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingAsyncTask.cancel(true);
                       searchCard.setVisibility(View.INVISIBLE);
                       SEARCH_STATE=false;

                    }
                });

            } else if (mapping.equals("ON") && listenerState) {

                final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser, this, progressBar, true);
                loadingAsyncTask.execute();


            }
        }
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
            switch (position){
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
        public CharSequence getPageTitle(int position){
            switch (position){
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
}
