package fusionkey.lowkey.main;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import fusionkey.lowkey.R;
import fusionkey.lowkey.ROOMdatabase.AppDatabase;
import fusionkey.lowkey.ROOMdatabase.MessagesActivity;
import fusionkey.lowkey.ROOMdatabase.UserDao;
import fusionkey.lowkey.listAdapters.ChatTabAdapters.ChatTabAdapter;
import fusionkey.lowkey.listAdapters.PagerAdapter.SampleAdapter;
import fusionkey.lowkey.models.UserD;

public class StartTab extends Fragment {
    private LinearLayout servicesLayout;
    private LinearLayout messagesLayout;
    private TextView msg;
    private TextView gst;
    View rootView;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
            rootView=inflater.inflate(R.layout.activity_profile_tab, container, false);

            servicesLayout = rootView.findViewById(R.id.Services);
            messagesLayout = rootView.findViewById(R.id.msgs);

             msg = (TextView) rootView.findViewById(R.id.messages);
             gst = (TextView) rootView.findViewById(R.id.getstarted);
        createLayout(1);
        gst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            createLayout(1);
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLayout(2);
                }
            });

            return rootView;
    }
    private void createLayout(int step) {
        switch (step) {
            case 1:
                services();
                break;
            case 2:
                messages();
                break;
            default:
                services();
                servicesLayout.setVisibility(View.VISIBLE);
                messagesLayout.setVisibility(View.GONE);
        }
    }
    private PagerAdapter buildAdapter() {
        return(new SampleAdapter(getActivity(),getChildFragmentManager()));
    }
    private void services(){
        servicesLayout.setVisibility(View.VISIBLE);
        messagesLayout.setVisibility(View.GONE);
        msg.setTextColor(Color.GRAY);
        gst.setTextColor(Color.BLACK);
        ViewPager pager=(ViewPager)rootView.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
    }
    private void messages(){
        msg.setTextColor(Color.BLACK);
        gst.setTextColor(Color.GRAY);
        servicesLayout.setVisibility(View.GONE);
        messagesLayout.setVisibility(View.VISIBLE);

        RecyclerView msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chatlist);
        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<UserD> userList = new ArrayList<UserD>();
        AppDatabase database = Room.databaseBuilder(getActivity(), AppDatabase.class, "user-database")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build();
        UserDao userDAO = database.userDao();
        userList = (ArrayList)userDAO.getAll();
        database.close();
        final ChatTabAdapter adapter = new ChatTabAdapter(userList);
        msgRecyclerView.setAdapter(adapter);
        adapter.setListener(new ChatTabAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserD item) {
                Intent intent = new Intent(getContext(), MessagesActivity.class);
                intent.putExtra("username", item.getUsername());
                getActivity().overridePendingTransition(0,0);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }

            @Override
            public boolean onLongClick(UserD item,int position) {
                AppDatabase database = Room.databaseBuilder(getActivity(), AppDatabase.class, "user-database")
                        .allowMainThreadQueries()   //Allows room to do operation on main thread
                        .build();
                UserDao userDAO = database.userDao();
                UserD user = new UserD();
                user = userDAO.findByName(item.getUsername());
                userDAO.delete(user);
                database.close();
                adapter.deleteItem(position);
                return true;
            }
        });
    }
}