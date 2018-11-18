package fusionkey.lowkey.newsfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.listAdapters.NotificationAdapters.NotificationAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pushnotifications.activities.CommentsFromNotificationActivity;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.LoadNotifPhotosAsync;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTO;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTOBuilder;
import fusionkey.lowkey.pushnotifications.notificationsV2.GroupingHighLevelAsync;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.GroupNotificationAbstract;

public class notificationFragment extends Fragment {
    private static final String KEY_POSITION = "position";

    NotificationAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerview;
    ArrayList<GroupNotificationAbstract> notif = new ArrayList<>();

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
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        adapter = new NotificationAdapter(notif, mRecyclerview, getContext());

        initAdapter();
        getData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initAdapter();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        return (rootView);
    }

    private void getData() {
        new GroupingHighLevelAsync(adapter, notif).execute();
    }

    private void initAdapter() {
        /*adapter.setOnLoadMoreListener(new NotificationAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                final GroupNotificationAbstract m = null;
                notif.add(m);
                adapter.notifyItemInserted(notif.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeItem(notif.indexOf(m));

                    }
                }, 2000);

            }
        });*/
        adapter.setListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupNotificationAbstract item) {
                Intent intent = new Intent(getActivity(), CommentsFromNotificationActivity.class);
                intent.putExtra("timestamp", item.getTimestamp());
                adapter.notifyDataSetChanged();
                startActivity(intent);
            }
        });
        mRecyclerview.setAdapter(adapter);
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