package fusionkey.lowkey.newsfeed;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.R;

import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.main.utils.PhotoUploader;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.asynctasks.NewsFeedAsyncTaskBuilder;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;

import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;
import fusionkey.lowkey.pointsAlgorithm.PointsCalculator;


public class NewsFeedTab extends Fragment {
    public static final int NEWS_FEED_PAGE_SIZE = 4;
    public static final int COMMENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int POST_QUESTION_ACTIVITY_REQUEST_CODE = 2;

    private NewsFeedAdapter adapter;
    private ArrayList<NewsFeedMessage> messages;

    private ConstraintLayout viewProfileCard;
    private CircleImageView ivProfile;
    private CircleImageView exit;
    private TextView viewUsername;
    private TextView viewPoints;


    private RecyclerView msgRecyclerView;
    private Button button;
    private NewsFeedRequest newsFeedRequest;
    private String uniqueID;
    public SwipeRefreshLayout swipeRefreshLayout;
    private Long referenceTimestamp;
    private String id;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_newsfeed, container, false);

        msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_listview);
        button = rootView.findViewById(R.id.email_sign_in_button2);

        viewProfileCard = rootView.findViewById(R.id.viewProfileCard);
        viewProfileCard.setVisibility(View.INVISIBLE);
        ivProfile = rootView.findViewById(R.id.ivProfile2);
        viewUsername = rootView.findViewById(R.id.textView8);
        viewPoints = rootView.findViewById(R.id.points);
        exit = rootView.findViewById(R.id.exit);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        UserDB attributes = LowKeyApplication.userManager.getUserDetails();
        id = attributes.getUsername();
        uniqueID = attributes.getUserEmail();
        newsFeedRequest = new NewsFeedRequest(uniqueID);

        initializeAdapterAndListData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewsFeedTab.this.getContext(), AskAQuestion.class);
                startActivityForResult(intent, POST_QUESTION_ACTIVITY_REQUEST_CODE);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNewsFeed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        startPopulateNewsFeed();
        return rootView;
    }



    private void initializeAdapterAndListData() {
        messages = new ArrayList<>();
        adapter = new NewsFeedAdapter(messages, getActivity().getApplicationContext(), msgRecyclerView);
        msgRecyclerView.setAdapter(adapter);

        adapter.setListener(new NewsFeedAdapter.OnItemClickListenerNews() {
            @Override
            public void onItemClick(ChatTabViewHolder item, View v) {
                try{
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                final Intent intent = new Intent(NewsFeedTab.this.getContext(), CommentsActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);

                MyParcelable object = new MyParcelable();
                intent.putExtra("parcel", object);
                intent.putExtra("anon", m.getAnon());
                intent.putExtra("SNStopic", m.getSNStopic());
                intent.putExtra("timestampID", m.getTimeStamp());
                intent.putExtra("body", m.getContent());
                intent.putExtra("title", m.getTitle());
                intent.putExtra("username", m.getUser());
                intent.putExtra("email", m.getId());

                if (m.getCommentArrayList() != null) {
                    object.setArrList(m.getCommentArrayList());
                } else {
                    object.setArrList(new ArrayList<Comment>());
                }

                startActivityForResult(intent, COMMENT_ACTIVITY_REQUEST_CODE);
                getActivity().overridePendingTransition(0, 0);
            }catch(ArrayIndexOutOfBoundsException ioobe){

                }

            }
        });

        adapter.setOnLoadMoreListener(new NewsFeedAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                final NewsFeedMessage m = null;
                messages.add(m);
                adapter.notifyItemInserted(messages.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeItem(messages.indexOf(m));
                        generateNextSetOfNewsFeedItems();
                    }
                }, 2000);

            }
        });
        adapter.setDeleteListener(new NewsFeedAdapter.OnDeleteItem() {
            @Override
            public void deleteItem(ChatTabViewHolder item, View v) {
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                new NewsFeedRequest(uniqueID).deleteQuestion(String.valueOf(m.getTimeStamp()));
                adapter.removeItem(position);
            }
        });
        adapter.setViewProfile(new NewsFeedAdapter.onViewProfile() {
            @Override
            public void viewProfile(ChatTabViewHolder item, View v) {
                viewProfileCard.setVisibility(View.VISIBLE);
                String userID = adapter.getMsg(item.getAdapterPosition()).getId();
                try {
                    if(adapter.getMsg(item.getAdapterPosition()).getAnon()) {
                        Picasso.with(getContext()).load(R.drawable.avatar_placeholder).into(ivProfile);
                        viewUsername.setText("Anonymous");
                    }
                    else {
                        Picasso.with(getContext()).load(adapter.getMsg(item.getAdapterPosition()).getFile()).into(ivProfile);
                        viewUsername.setText(adapter.getMsg(item.getAdapterPosition()).getUser());
                    }
                } catch (NullPointerException npe){
                    Picasso.with(getContext()).load(R.drawable.avatar_placeholder).into(ivProfile);
                }

                UserAttributeManager userAttributeManager = new UserAttributeManager(userID);
                UserDB user = userAttributeManager.getUserDB();
                String displayPoints = "Has " + new DecimalFormat("##.####").format(user.getScore()) + " points";
                viewPoints.setText(displayPoints);

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewProfileCard.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @TODO habar nu am de ce prinde ... dupa ce dai post la o intrebare si lasi comment | won't update the counter - parcelable failed
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMMENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();

                try {
                    MyParcelable object = b.getParcelable("NewComments");
                    Long timestampID = b.getLong("ItemID");
                    List<Comment> commentArrayList = object.getArrList();
                    for (NewsFeedMessage m : messages) {
                        if (m.getTimeStamp().equals(timestampID)) {
                            for (Comment c : commentArrayList)
                                m.addCommentToList(c);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (NullPointerException e) {
                    Log.e("Error", "parcelable object failed");
                }
            } else {
                Log.e("IntentResult", "No comments to update");
            }
        }
        if (requestCode == POST_QUESTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {


                final NewsFeedMessage nfm = createNewsFeedMessageFromActivityResult(data);
                newsFeedRequest.postQuestion(nfm, new IGenericConsumer<JSONObject>() {
                    @Override
                    public void consume(JSONObject item) {
                        nfm.setSNSTopicFromResponse(item);
                        adaptUIWithNewNewsFeedMessage(nfm);
                    }
                });

            } else {
                Log.e("IntentRes-PostQuestion", "Result NOT OK -> NewsFeedMessage" +
                        "could not be added/posted");
            }
        }
    }

    private NewsFeedMessage createNewsFeedMessageFromActivityResult(Intent data) {
        Bundle b = data.getExtras();
        try {

            NewsFeedMessage m11 = new NewsFeedMessage();
            m11.setAnon(b.getBoolean("anonQ"));
            m11.setUser(id);
            m11.setTimeStamp(b.getLong("TimestampQ"));
            m11.setTitle(b.getString("TitleQ"));
            m11.setContent(b.getString("BodyQ"));
            m11.setId(uniqueID);
            m11.setType(NewsFeedMessage.NORMAL);

            return m11;
        } catch (NullPointerException e) {
            Log.e("createNewsFeedMessage", "parcelable object failed");
            return null;
        }
    }

    private void adaptUIWithNewNewsFeedMessage(NewsFeedMessage nfm) {
        messages.add(0, nfm);
        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(0);
    }

    private void startPopulateNewsFeed() {
        if (NetworkManager.isNetworkAvailable()) {
            new NewsFeedAsyncTaskBuilder(newsFeedRequest, messages, msgRecyclerView, adapter)
                    .addIsStart()
                    .addArePostNew()
                    .addSetter(new IGenericConsumer<Long>() {
                        @Override
                        public void consume(Long item) {
                            if (item != null)
                                referenceTimestamp = item;
                            else
                                Toast.makeText(NewsFeedTab.this.getContext(),
                                        NewsFeedTab.this.getResources().getString(R.string.nf_no_posts),
                                        Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build()
                    .execute();
        } else Toast.makeText(getActivity(),
                getActivity().getString(R.string.no_network_message),
                Toast.LENGTH_SHORT).show();
    }

    private void generateNextSetOfNewsFeedItems() {
        new NewsFeedAsyncTaskBuilder(newsFeedRequest, messages, msgRecyclerView, adapter)
                .addArePostNew()
                .addSetter(new IGenericConsumer<Long>() {
                    @Override
                    public void consume(Long item) {
                        if (item != null)
                            referenceTimestamp = item;
                        else
                            Toast.makeText(NewsFeedTab.this.getContext(),
                                    NewsFeedTab.this.getResources().getString(R.string.nf_no_more_posts),
                                    Toast.LENGTH_SHORT).show();
                    }
                })
                .addReferenceTimeSTamp(referenceTimestamp)
                .build()
                .execute();
    }

    public void refreshNewsFeed() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        initializeAdapterAndListData();
        startPopulateNewsFeed();
        PhotoUploader.deleteFolder();
    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();
        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}