package fusionkey.lowkey.main;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.NewsfeedAdapter;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.newsfeed.NewsFeedMessage;
import fusionkey.lowkey.R;
import fusionkey.lowkey.newsfeed.NewsFeedAsyncTask;
import fusionkey.lowkey.newsfeed.NewsfeedRequest;

public class NewsFeedTab extends Fragment{
    NewsfeedAdapter adapter;
    ArrayList<NewsFeedMessage> messages;

    private RecyclerView msgRecyclerView;
    EditText title;
    EditText body;
    Button send;
    ImageView exp;
    ImageView col;
    CheckBox checkBox;
    NewsfeedRequest newsfeedRequest;
    String uniqueID;
    public SwipeRefreshLayout swipeRefreshLayout;

    private Paint p = new Paint();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_newsfeed, container, false);
        msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_listview);
        final View v1 = rootView.findViewById(R.id.v1);
        final View v2 = rootView.findViewById(R.id.v2);
        exp = rootView.findViewById(R.id.expand);
        col = rootView.findViewById(R.id.collapse);
        title = rootView.findViewById(R.id.title);
        send = rootView.findViewById(R.id.button2);
        body = rootView.findViewById(R.id.body);
        checkBox = rootView.findViewById(R.id.checkBox);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        CardView card = rootView.findViewById(R.id.card);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        //Getting user details from Cognito
        Map<String, String> attributes = LowKeyApplication.userManager.getUserDetails().getAttributes().getAttributes();
        final String id = attributes.get(UserAttributesEnum.USERNAME.toString());
        uniqueID = (attributes.get(UserAttributesEnum.EMAIL.toString()));
        // Create the initial data list.
        messages = new ArrayList<NewsFeedMessage>();
        adapter = new NewsfeedAdapter(messages);
        msgRecyclerView.setAdapter(adapter);
        newsfeedRequest = new NewsfeedRequest(uniqueID);

        exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(v2,500,500);
            }
        });

        col.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse(v2,500,1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean anon = checkBox.isChecked();
                if(!title.getText().toString().equals("") && !body.getText().toString().equals("")){
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    newsfeedRequest.postQuestion(timestamp.getTime(),anon,String.valueOf(title.getText()),String.valueOf(body.getText()));
                    NewsFeedMessage m11 = new NewsFeedMessage();
                    m11.setAnon(anon);m11.setUser(id);m11.setDate(String.valueOf(timestamp.getTime()));
                    m11.setTitle(title.getText().toString());m11.setContent(String.valueOf(body.getText()));
                    m11.setId(uniqueID);
                    messages.add(m11);
                    int newMsgPosition = messages.size() - 1;
                    adapter.notifyItemInserted(newMsgPosition);
                    collapse(v2,1000,1);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNewsfeed();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        initSwipe();

        adapter.setListener(new NewsfeedAdapter.OnItemClickListenerNews() {
            @Override
            public void onItemClick(ChatTabViewHolder item) {
                item.view.setVisibility(View.VISIBLE);
                expand(item.view,500,400);
            }
            @Override
            public boolean onLongClick(ChatTabViewHolder item,int position) {
                collapse(item.view,1000,1);
                return true;
            }
        });

        refreshNewsfeed();
        adapter.notifyDataSetChanged();

        return rootView;
    }

    public void refreshNewsfeed(){
        NewsFeedAsyncTask newsFeedAsyncTask = new NewsFeedAsyncTask(messages,msgRecyclerView,adapter,newsfeedRequest);
        newsFeedAsyncTask.execute();
        adapter.notifyDataSetChanged();
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
                            new NewsfeedRequest(uniqueID).deleteQuestion(m.getDate());
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

}