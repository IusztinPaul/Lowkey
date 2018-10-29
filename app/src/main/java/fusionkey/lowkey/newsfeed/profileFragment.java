package fusionkey.lowkey.newsfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.MainCallback;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pointsAlgorithm.PointsCalculator;

public class profileFragment extends Fragment {

    private static final String KEY_POSITION="position";
    private MainCallback mainCallback;
    SharedPreferences sharedPreferences;


    TextView points;
    TextView helped;
    TextView money;
    TextView payment;

    TextView level;
    TextView showall;
    ProgressBar paymentBar;
    ProgressBar expBar;
    NewsFeedAdapter adapter;
    ArrayList<NewsFeedMessage> messages;
    String uniqueID;
    private RecyclerView msgRecyclerView;
    NewsFeedRequest newsfeedRequest;

    static profileFragment newInstance(int position) {
        profileFragment frag=new profileFragment();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mainCallback = (MainCallback) context;
        }catch(ClassCastException castException){
            castException.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.profile_main, container, false);
        points = rootView.findViewById(R.id.points);
        helped = rootView.findViewById(R.id.helped);
        money = rootView.findViewById(R.id.money);
        payment = rootView.findViewById(R.id.payment);
        level = rootView.findViewById(R.id.level);
        expBar = rootView.findViewById(R.id.expBar);
        paymentBar = rootView.findViewById(R.id.paymentBar);
        showall = rootView.findViewById(R.id.showall);


        UserDB attributes = LowKeyApplication.userManager.getUserDetails();
        uniqueID = attributes.getUserEmail();

        populateUI();
        return(rootView);
    }

    public void populateUI(){
        try {
            UserDB attributes = LowKeyApplication.userManager.getUserDetails();
            Long pointsS = attributes.getScore();

            if(pointsS==null)
                pointsS = 0L;

            Double experience = (double) pointsS;

            points.setText(pointsS != 0L ? pointsS + "" : "");

            paymentBar.setMax(2500);
            paymentBar.setProgress((int)experience.doubleValue());
           // setExpBar((int)experience.doubleValue());
            String moneyS = String.valueOf(PointsCalculator.calculatePointsForMoney(experience))+"$";
            money.setText(moneyS != null ? moneyS : "");
            String p = "Chat points gained: "+ pointsS + " / 2,500";
            payment.setText(p);
        } catch (NullPointerException e) {
            Log.e("NullPointerExp", "User details not loaded yet");
        }
    }
    private void setExpBar(int points){
        if(points>=0 && points < 10){
            expBar.setMax(9);
            expBar.setProgress(points);
            level.setText("Helper level 0");
        }
        if(points>=10 && points < 50){
            expBar.setMax(49);
            expBar.setProgress(points);
            level.setText("Helper level 1");
        }
        if(points>=49 && points < 100){
            expBar.setMax(99);
            expBar.setProgress(points);
            level.setText("Helper level 2");
        }
        if(points>=100 && points < 500){
            expBar.setMax(499);
            expBar.setProgress(points);
            level.setText("Helper level 3");
        }
        if(points>=499 && points < 500){
            expBar.setMax(499);
            expBar.setProgress(points);
            level.setText("Helper level 4");
        }
    }

}