package fusionkey.lowkey.newsfeed;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.R;

import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.newsfeed.asynctasks.NewsFeedAsyncTaskBuilder;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;

import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;


public class NewsFeedTab extends Fragment {
    public static final int NEWS_FEED_PAGE_SIZE = 4;
    public static final int COMMENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int POST_QUESTION_ACTIVITY_REQUEST_CODE = 2;

    private NewsFeedAdapter adapter;
    private ArrayList<NewsFeedMessage> messages;

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

        swipeRefreshLayout = rootView.findViewById(R.id.swipe);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        // Getting user details from Cognito.
        UserDB attributes = LowKeyApplication.userManager.getUserDetails();
        id = attributes.getUsername();
        uniqueID = attributes.getUserEmail();

        // Create the initial data list.
        messages = new ArrayList<>();
        adapter = new NewsFeedAdapter(messages, getActivity().getApplicationContext(), msgRecyclerView);
        msgRecyclerView.setAdapter(adapter);
        newsFeedRequest = new NewsFeedRequest(uniqueID);


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
                //refreshNewsFeed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter.setListener(new NewsFeedAdapter.OnItemClickListenerNews() {
            @Override
            public void onItemClick(ChatTabViewHolder item, View v) {
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
            }
        });

        startPopulateNewsFeed();
        adapter.notifyDataSetChanged();

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
                        // Generate the next set of items.
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

        return rootView;
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
                Long timestampID = null;
                List<Comment> commentArrayList = null;

                try {
                    MyParcelable object = b.getParcelable("NewComments");
                    timestampID = b.getLong("ItemID");
                    commentArrayList = object.getArrList();
                } catch (NullPointerException e) {
                    Log.e("Error", "parcelable object failed");
                }

                try {
                    NewsFeedMessage newsFeedMessage = getNewsFeedMessageForTimestamp(timestampID);
                    for (Comment c : commentArrayList)
                        newsFeedMessage.addCommentToList(c);
                    adapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    Log.e("AddCommentsToPost", e.getMessage());
                }

            } else {
                Log.e("IntentResult", "No comments to update");
            }
        } else if (requestCode == POST_QUESTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                final NewsFeedMessage newsFeedMessage =
                        createNewsFeedMessageFromActivityResult(data);

                newsFeedRequest.postQuestion(newsFeedMessage,
                        new IGenericConsumer<JSONObject>() {
                            @Override
                            public void consume(JSONObject item) {
                                messages.add(0, newsFeedMessage);
                                adapter.notifyDataSetChanged();
                                adapter.notifyItemInserted(0);
                            }
                        });

                Log.e("Q", "ADDEEDDDDDd");

            } else {
                Log.e("POST_REQUEST_CODE", "No comments to update");
            }
        }
    }

    private NewsFeedMessage getNewsFeedMessageForTimestamp(Long timestamp) {
        if(timestamp == null)
            return null;

        for (NewsFeedMessage m : messages)
            if (m.getTimeStamp().equals(timestamp))
                return m;

        return null;
    }

    private NewsFeedMessage createNewsFeedMessageFromActivityResult(Intent data) {
        Bundle b = data.getExtras();
        try {
            final NewsFeedMessage m11 = new NewsFeedMessage();
            m11.setAnon(b.getBoolean("anonQ"));
            m11.setUser(id);
            m11.setTimeStamp(b.getLong("TimestampQ"));
            m11.setTitle(b.getString("TitleQ"));
            m11.setContent(b.getString("BodyQ"));
            m11.setId(uniqueID);
            m11.setType(NewsFeedMessage.NORMAL);

            return m11;
        } catch (NullPointerException e) {
            Log.e("Error", "parcelable object failed");
            return null;
        }
    }

    public void startPopulateNewsFeed() {
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

    public void refreshNewsFeed() {
        for (int i = 0; i < messages.size(); i += NEWS_FEED_PAGE_SIZE)
            if (messages.get(i) != null)
                new NewsFeedAsyncTaskBuilder(newsFeedRequest, messages, msgRecyclerView, adapter)
                        .addIsStart()
                        .addReferenceTimeSTamp(messages.get(i).getTimeStamp())
                        .build()
                        .execute();
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
