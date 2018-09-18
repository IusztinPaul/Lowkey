package fusionkey.lowkey.main;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.auth.utils.UserDBManager;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;

import fusionkey.lowkey.main.menu.Menu;

import fusionkey.lowkey.main.menu.profile.EditUserActivity;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.pointsAlgorithm.PointsCalculator;


public class ProfileTab extends Fragment {
    TextView points;
    TextView helped;
    TextView money;
    TextView payment;
    TextView username;
    TextView level;
    ProgressBar paymentBar;
    ProgressBar expBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chat, container, false);
        final Button logOut = (Button) rootView.findViewById(R.id.button);
        points = rootView.findViewById(R.id.points);
        helped = rootView.findViewById(R.id.helped);
        money = rootView.findViewById(R.id.money);
        payment = rootView.findViewById(R.id.payment);
        username = rootView.findViewById(R.id.username);
        level = rootView.findViewById(R.id.level);
        expBar = rootView.findViewById(R.id.expBar);
        paymentBar = rootView.findViewById(R.id.paymentBar);
        ImageView settings = (ImageView) rootView.findViewById(R.id.settingsImg);
        populateUI();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().startActivity(new Intent(getContext().getApplicationContext(), Menu.class));
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkManager.isNetworkAvailable())
                logOut();
                else Toast.makeText(getActivity(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

            }
        });

        final ImageView imageViewEdit = rootView.findViewById(R.id.imageViewEdit);
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  UserDBManager.create("Andrei");
                 UserDBManager.update("Paul", 20);
                  UserDBManager.update("Sebi", 50);
                //Log.e("USEEEER", UserDBManager.getUserData("Paul").toString());
                //UserDBManager.delete("Andrei");


                Intent intent = new Intent(ProfileTab.this.getContext(), EditUserActivity.class);
                startActivity(intent);
            }
        });

        final CircleImageView circleImageView = rootView.findViewById(R.id.circleImageView);
        if(LowKeyApplication.profilePhoto != null)
            circleImageView.setImageBitmap(LowKeyApplication.profilePhoto);
        else
            circleImageView.setBackgroundResource(R.drawable.avatar_placeholder);

        return rootView;
    }

    private void logOut(){
        LowKeyApplication.userManager.logout();
        getActivity().startActivity(new Intent(getContext().getApplicationContext(), EntryActivity.class));
    }
    public void populateUI(){
        try {
            Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
            String usernameS = attributes.get(UserAttributesEnum.USERNAME.toString()),
                    pointsS = attributes.get(UserAttributesEnum.SCORE.toString());
            if(pointsS==null)
                pointsS = "0";
            Double experience = Double.parseDouble(pointsS);

            username.setText(usernameS != null ? usernameS : "");
            points.setText(pointsS != null ? pointsS : "");

            paymentBar.setMax(2500);
            paymentBar.setProgress((int)experience.doubleValue());
            setExpBar((int)experience.doubleValue());
            String moneyS = String.valueOf(PointsCalculator.calculatePointsForMoney(Double.parseDouble(pointsS)))+"$";
            money.setText(moneyS != null ? moneyS : "");
            String p = "Chat points gained: "+ pointsS + " / 2,500";
            payment.setText(p);
        } catch (NullPointerException e) {
            Log.e("NullPointerExp", "User details not loaded yet");
        }
    }
    @Override
    public void onPause() {
        Log.e("onPauseProfile", "called");
        super.onPause();
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