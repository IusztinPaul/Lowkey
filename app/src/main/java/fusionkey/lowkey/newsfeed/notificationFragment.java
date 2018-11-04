package fusionkey.lowkey.newsfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.LoginActivity;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.listAdapters.NotificationAdapters.NotificationAdapter;
import fusionkey.lowkey.pushnotifications.activities.CommentsFromNotificationActivity;
import fusionkey.lowkey.pushnotifications.models.NotificationTO;

public class notificationFragment extends Fragment {
    private static final String KEY_POSITION = "position";

    SharedPreferences sharedPreferences;
    NotificationAdapter adapter;
    ArrayList<NotificationTO> mNotification = new ArrayList<>();
    private RecyclerView mRecyclerview;

    String id = LowKeyApplication.userManager.getCachedEmail();

    static notificationFragment newInstance(int position) {
        notificationFragment frag = new notificationFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return (frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_notifications, container, false);
        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.notifRec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerview.setLayoutManager(linearLayoutManager);


        adapter = new NotificationAdapter(getNotifications());
        adapter.setListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotificationTO item) {
                Intent intent = new Intent(getActivity(), CommentsFromNotificationActivity.class);
                intent.putExtra("timestamp", item.getTimestamp());
                startActivity(intent);
            }
        });
        mRecyclerview.setAdapter(adapter);

        return (rootView);
    }
    private ArrayList<NotificationTO> getNotifications(){
        ArrayList<NotificationTO> notif = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        int counter = preferences.getInt(id,0);
        for(int i=0;i<counter;i++) {
            String[] s = preferences.getString(id+i,"").split("muiepsdasdfghjkl");
            notif.add(new NotificationTO(s[2].replace("}",""),s[0].replace("{default=","") + " answered your question "+ s[1],s[1]));

        }
        Collections.reverse(notif);
        return notif;
    }
    private String localTime(Long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMMM - HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(time);
        return sf.format(date);
    }

}