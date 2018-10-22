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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.R;

import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.newsfeed.asynctasks.NewsFeedAsyncTaskBuilder;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;

import fusionkey.lowkey.newsfeed.models.Comment;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;


public class NewsFeedTab extends Fragment{
    public static final int NEWS_FEED_PAGE_SIZE = 4;
    public static final int COMMENT_ACTIVITY_REQUEST_CODE = 1;

    private NewsFeedAdapter adapter;
    private ArrayList<NewsFeedMessage> messages;

    private RecyclerView msgRecyclerView;
    private EditText title;
    private EditText body;
    private Button send;
    private ImageView exp;
    private ImageView col;
    private CheckBox checkBox;
    private Button button;
    private NewsFeedRequest newsFeedRequest;
    private String uniqueID;
    public SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView circleImageView;
    private Long referenceTimestamp;

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
        Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
        final String id = attributes.get(UserAttributesEnum.USERNAME.toString());
        uniqueID = (attributes.get(UserAttributesEnum.EMAIL.toString()));

        // Create the initial data list.



        messages = new ArrayList<>();
        adapter = new NewsFeedAdapter(messages,getActivity().getApplicationContext(),msgRecyclerView);
        msgRecyclerView.setAdapter(adapter);
        newsFeedRequest = new NewsFeedRequest(uniqueID);


        /*
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean anon = checkBox.isChecked();
                if(!title.getText().toString().equals("") && !body.getText().toString().equals("")){

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    NewsFeedMessage m11 = new NewsFeedMessage();

                    newsFeedRequest.postQuestion(timestamp.getTime(),anon,String.valueOf(title.getText()),String.valueOf(body.getText()));


                    m11.setAnon(anon);m11.setUser(id);m11.setTimeStamp(timestamp.getTime());
                    m11.setTitle(title.getText().toString());m11.setContent(String.valueOf(body.getText()));
                    m11.setId(uniqueID);
                    m11.setType(NewsFeedMessage.NORMAL);

                    messages.add(m11);
                    adapter.notifyDataSetChanged();
                    collapse(v2,1000,1);
                }
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewsFeedTab.this.getContext(), Askaquestion.class);
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
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
            public void onItemClick(ChatTabViewHolder item,View v) {
                int position = item.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                final Intent intent = new Intent(NewsFeedTab.this.getContext(), CommentsActivity.class);
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
                                            if(item != null)
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
                    }, 5000);

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
     * @TODO habar nu am de ce prinde ... dupa ce dai post la o intrebare si lasi comment | won't update the counter - parcelable failed
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
             if (requestCode == COMMENT_ACTIVITY_REQUEST_CODE) {
                 if(resultCode == Activity.RESULT_OK){
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
            }else { Log.e("IntentResult","No comments to update");
                 }
             }

    }

    public void startPopulateNewsFeed() {
        new NewsFeedAsyncTaskBuilder(newsFeedRequest, messages, msgRecyclerView, adapter)
                .addIsStart()
                .addArePostNew()
                .addSetter(new IGenericConsumer<Long>() {
                    @Override
                    public void consume(Long item) {
                        if(item != null)
                            referenceTimestamp = item;
                        else
                            Toast.makeText(NewsFeedTab.this.getContext(),
                                    NewsFeedTab.this.getResources().getString(R.string.nf_no_posts),
                                    Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .execute();
    }

    public void refreshNewsFeed() {
        for(int i = 0; i < messages.size(); i += NEWS_FEED_PAGE_SIZE)
            if(messages.get(i) != null)
                new NewsFeedAsyncTaskBuilder(newsFeedRequest, messages, msgRecyclerView, adapter)
                        .addIsStart()
                        .addReferenceTimeSTamp(messages.get(i).getTimeStamp())
                        .build()
                        .execute();
    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();
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
        int prevHeight  = v.getHeight();
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

/*
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView MsgrecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NewsFeedMessage m = adapter.getMsg(position);
                if (direction == ItemTouchHelper.LEFT){
                    adapter.removeItem(position);
                        if(m.getId().equals(uniqueID))
                            new NewsFeedRequest(uniqueID).deleteQuestion(m.getTimeStamp());
                } else {
                    //removeView();
                    adapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){

                        p.setColor(Color.parseColor("#FFFFFF"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.selecttab1);
                        icon = drawableToBitmap(drawable);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#FFFFFF"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.selecttab2);
                        icon = drawableToBitmap(drawable);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(msgRecyclerView);
    }

    private void removeView(){
        if(getParentFragment().getView().getParent()!=null) {
            ((ViewGroup) getParentFragment().getView().getParent()).removeView(getParentFragment().getView());
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

*/
}