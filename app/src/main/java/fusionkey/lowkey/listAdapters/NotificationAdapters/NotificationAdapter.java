package fusionkey.lowkey.listAdapters.NotificationAdapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.listAdapters.LoadingViewHolder;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTO;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.GroupNotificationAbstract;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GroupNotificationAbstract> mNotifications;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private Context context;
    private boolean isLoading;

    private OnItemClickListener listener;
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnItemClickListener {
        void onItemClick(GroupNotificationAbstract item);

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public NotificationAdapter(ArrayList<GroupNotificationAbstract> mNotifications, RecyclerView recyclerView, Context context) {
        this.mNotifications = mNotifications;
        this.context = context;
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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
            return new NotificationViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            final GroupNotificationAbstract notification = this.mNotifications.get(position);
            final NotificationViewHolder holder1 = (NotificationViewHolder) holder;

            holder1.name.setText(notification.mapTheString());
            holder1.data.setText(notification.getNotification().getComment());

            Picasso.with(context).load(notification.getFile()).into(holder1.pic);


            holder1.bind(mNotifications.get(position), listener);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void removeItem(int position) {
        if (position != -1 && position < mNotifications.size()) {
            mNotifications.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mNotifications.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mNotifications.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mNotifications != null ? mNotifications.size() : 0;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void clear() {
        mNotifications.clear();
        notifyDataSetChanged();
    }


}