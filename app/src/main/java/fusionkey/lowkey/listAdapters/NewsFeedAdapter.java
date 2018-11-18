package fusionkey.lowkey.listAdapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;

import static java.security.AccessController.getContext;

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    private List<NewsFeedMessage> mMessages;
    private static String ANON_STRING = "Anonymous";
    private Context mcontext;
    private OnLoadMoreListener onLoadMoreListener;
    public OnItemClickListenerNews listener;
    public onViewProfile viewProfileListener;
    private OnDeleteItem del;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;

    public NewsFeedAdapter(List<NewsFeedMessage> mMessages, Context context, RecyclerView recyclerView) {
        this.mMessages = mMessages;
        this.mcontext = context;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            }
        });

    }

    public interface OnItemClickListenerNews {
        void onItemClick(ChatTabViewHolder item, View v);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnDeleteItem {
        void deleteItem(ChatTabViewHolder item, View v);
    }

    public interface onViewProfile {
        void viewProfile(ChatTabViewHolder item,View v);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item, parent, false);
            return new ChatTabViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ChatTabViewHolder) {
            final NewsFeedMessage msgDto = this.mMessages.get(position);
            final ChatTabViewHolder chatTabViewHolder = (ChatTabViewHolder) holder;
            if (msgDto.getType().equals(NewsFeedMessage.NORMAL)) {
                chatTabViewHolder.type.setVisibility(View.VISIBLE);
                chatTabViewHolder.bindDelete(chatTabViewHolder, del);
                chatTabViewHolder.image.setImageBitmap(msgDto.getUserPhoto());
            } else {
                chatTabViewHolder.type.setVisibility(View.GONE);
            }
            chatTabViewHolder.title.setText(msgDto.getTitle());
            //chatTabViewHolder.image.setImageBitmap(msgDto.getUserPhoto());
            if (!msgDto.getAnon()) {
                chatTabViewHolder.name.setText(msgDto.getUser());
                if (msgDto.getType().equals(NewsFeedMessage.NORMAL)) {
                    Picasso.with(mcontext).load(LowKeyApplication.userManager.photoFile).into(chatTabViewHolder.image);
                } else {
                 if(msgDto.getFile()==null)
                        Picasso.with(mcontext).load(R.drawable.avatar_placeholder).into(chatTabViewHolder.image);
                    else
                        Picasso.with(mcontext).load(msgDto.getFile()).into(chatTabViewHolder.image);

                }
            } else {
                Picasso.with(mcontext).load(R.drawable.avatar_placeholder).into(chatTabViewHolder.image);
                chatTabViewHolder.name.setText(ANON_STRING);
            }
            chatTabViewHolder.lastmsg.setText(msgDto.getContent());

            chatTabViewHolder.date.setText(localTime(msgDto.getTimeStamp()));
            if (msgDto.getCommentArrayList() != null) {
                chatTabViewHolder.answers.setText(msgDto.getCommentArrayList().size() + " Answers");
            } else {
                chatTabViewHolder.answers.setText("0 Answers");
            }
            chatTabViewHolder.bind(chatTabViewHolder, listener);
            chatTabViewHolder.bindViewProfile(chatTabViewHolder,viewProfileListener);
            //Picasso doing the Cached Magic

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public NewsFeedAdapter(ArrayList users) {
        mMessages = users;
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    public void setViewProfile(onViewProfile listener) { this.viewProfileListener = listener;}

    public void setListener(OnItemClickListenerNews listener) {
        this.listener = listener;
    }

    public void setDeleteListener(OnDeleteItem listener) {
        this.del = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    private String localTime(Long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMMM - HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(time);
        PrettyTime t = new PrettyTime(date);
        return t.format(new Date());
    }

    public void removeItem(int position) {
        if(position != -1 && position < mMessages.size()) {
            mMessages.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mMessages.size());
        }
    }

    public NewsFeedMessage getMsg(int position) {
        if(position < 0)
            return new NewsFeedMessage();

        return mMessages.get(position);
    }

    public void addItem(NewsFeedMessage msg) {
        mMessages.add(msg);
        notifyItemInserted(mMessages.size());
    }

    public int getPosition(NewsFeedMessage msg) {
        return mMessages.indexOf(msg);
    }

}