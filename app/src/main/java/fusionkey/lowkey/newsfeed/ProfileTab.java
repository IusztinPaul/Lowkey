package fusionkey.lowkey.newsfeed;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;

import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
//import fusionkey.lowkey.listAdapters.NewsfeedAdapter;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.menu.Menu;

import fusionkey.lowkey.main.menu.profile.EditUserActivity;
import fusionkey.lowkey.main.utils.NetworkManager;

import fusionkey.lowkey.newsfeed.asynctasks.GetYourQuestionsAsyncTask;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
//import fusionkey.lowkey.newsfeed.util.NewsfeedRequest;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pointsAlgorithm.PointsCalculator;



public class ProfileTab extends Fragment {
    TextView points;
    TextView helped;
    TextView money;
    TextView payment;
    TextView username;
    TextView level;
    TextView showall;
    ProgressBar paymentBar;
    ProgressBar expBar;
    NewsFeedAdapter adapter;
    ArrayList<NewsFeedMessage> messages;
    String uniqueID;
    private RecyclerView msgRecyclerView;
    NewsFeedRequest newsfeedRequest;

    private static int showVar;

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
        showall = rootView.findViewById(R.id.showall);

        msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
        final String id = attributes.get(UserAttributesEnum.USERNAME.toString());
        uniqueID = (attributes.get(UserAttributesEnum.EMAIL.toString()));

        messages = new ArrayList<>();
        adapter = new NewsFeedAdapter(messages,getActivity().getApplicationContext(),msgRecyclerView);
        msgRecyclerView.setAdapter(adapter);
        newsfeedRequest = new NewsFeedRequest(uniqueID);

        adapter.setListener(new NewsFeedAdapter.OnItemClickListenerNews() {
            @Override
            public void onItemClick(ChatTabViewHolder item, View v) {
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                final Intent intent = new Intent(getActivity().getApplicationContext(), CommentsActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                if(m.getCommentArrayList()!=null) {
                    MyParcelable object = new MyParcelable();
                    object.setArrList(m.getCommentArrayList());
                    object.setMyInt(m.getCommentArrayList().size());
                    intent.putExtra("parcel", object);

                    intent.putExtra("timestampID",m.getTimeStamp());
                }

                startActivityForResult(intent,1);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        adapter.setDeleteListener(new NewsFeedAdapter.OnDeleteItem() {
            @Override
            public void deleteItem(ChatTabViewHolder item, View v) {
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                new NewsFeedRequest(uniqueID).deleteQuestion(m.getTimeStamp().toString());
                adapter.removeItem(position);
            }
        });
        showall.setText("show");
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showVar == 0){
                    refreshNewsfeed();
                    adapter.notifyDataSetChanged();
                    showVar = 1;
                    showall.setText("hide all your questions");
                }
                else if (showVar == 1){
                    showall.setText("show");
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    showVar = 0;
                }
            }
        });
        ImageView settings = (ImageView) rootView.findViewById(R.id.settingsImg);
        populateUI();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("token : ",FirebaseInstanceId.getInstance().getToken());

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
    public void refreshNewsfeed(){
        GetYourQuestionsAsyncTask getYourQuestionsAsyncTask = new GetYourQuestionsAsyncTask(messages,msgRecyclerView,adapter,newsfeedRequest);
        getYourQuestionsAsyncTask.execute();
        //adapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.e("GETHERE", "HERE");
            if(resultCode == Activity.RESULT_OK){
                Bundle b = data.getExtras();
                try {
                    MyParcelable object = b.getParcelable("NewComments");
                    String timestampID = b.getString("ItemID");
                    List<Comment> commentArrayList = object.getArrList();
                    for (NewsFeedMessage m : messages) {
                        Log.e("GETHERE", "HERE FOR ");
                        if (m.getTimeStamp().toString().equals(timestampID)) {
                            Log.e("GETHERE", "HERE IF");
                            for (Comment c : commentArrayList)
                                m.addCommentToList(c);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (NullPointerException e) {
                    Log.e("Error", "parcelable object failed");
                }
            }else { Log.e("IntentResult","No comments to update");
            }
        }

    }
}
