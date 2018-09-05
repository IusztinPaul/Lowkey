package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fusionkey.lowkey.main.StartTab;

public class ViewPagerIndicatorActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content,
                            new StartTab()).commit();
        }
    }
}