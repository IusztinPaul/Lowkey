package fusionkey.lowkey.newsfeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.MainCallback;
import fusionkey.lowkey.newsfeed.asynctasks.GetYourQuestionsAsyncTask;
import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

import static fusionkey.lowkey.newsfeed.NewsFeedTab.COMMENT_ACTIVITY_REQUEST_CODE;

public class questionsFragment extends Fragment {
    private static final String KEY_POSITION="position";
    SharedPreferences sharedPreferences;
    private MainCallback mainCallback;
    NewsFeedAdapter adapter;
    ArrayList<NewsFeedMessage> messages;
    String uniqueID;
    private RecyclerView msgRecyclerView;
    NewsFeedRequest newsfeedRequest;


    static questionsFragment newInstance(int position) {
        questionsFragment frag=new questionsFragment();
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
        View rootView=inflater.inflate(R.layout.profile_questions, container, false);
        msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        uniqueID = LowKeyApplication.userManager.getCachedEmail();

        messages = new ArrayList<>();
        adapter = new NewsFeedAdapter(messages,getActivity().getApplicationContext(),msgRecyclerView);
        msgRecyclerView.setAdapter(adapter);
        newsfeedRequest = new NewsFeedRequest(uniqueID);

        adapter.setListener(new NewsFeedAdapter.OnItemClickListenerNews() {
            @Override
            public void onItemClick(ChatTabViewHolder item, View v) {
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                final Intent intent = new Intent(questionsFragment.this.getContext(), CommentsActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                if(m.getCommentArrayList()!=null) {
                    MyParcelable object = new MyParcelable();
                    object.setArrList(m.getCommentArrayList());
                    intent.putExtra("parcel", object);
                    intent.putExtra("SNStopic",m.getSNStopic());
                    intent.putExtra("timestampID",m.getTimeStamp());
                    intent.putExtra("body",m.getContent());
                    intent.putExtra("title",m.getTitle());
                    intent.putExtra("username",m.getUser());
                }else {
                    MyParcelable object = new MyParcelable();
                    object.setArrList(new ArrayList<Comment>());
                    intent.putExtra("parcel", object);
                    intent.putExtra("SNStopic",m.getSNStopic());
                    intent.putExtra("timestampID",m.getTimeStamp());
                    intent.putExtra("body",m.getContent());
                    intent.putExtra("title",m.getTitle());
                    intent.putExtra("username",m.getUser());

                }
                startActivityForResult(intent, COMMENT_ACTIVITY_REQUEST_CODE);
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
        refreshNewsfeed();
        return(rootView);
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
