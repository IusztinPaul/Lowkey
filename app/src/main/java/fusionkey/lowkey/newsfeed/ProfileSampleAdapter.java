package fusionkey.lowkey.newsfeed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProfileSampleAdapter extends FragmentPagerAdapter {
    Context ctxt=null;
    private String[] tabTitles = new String[]{"Profile", "Questions","Notifications"};

    public ProfileSampleAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return(profileFragment.newInstance(position));
        if(position==1)
            return(questionsFragment.newInstance(position));
        else
            return(notificationFragment.newInstance(position));
    }

    @Override
    public String getPageTitle(int position) {
        return tabTitles[position];
    }
}