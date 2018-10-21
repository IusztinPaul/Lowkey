package fusionkey.lowkey.listAdapters.NotificationAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import fusionkey.lowkey.R;
import fusionkey.lowkey.listAdapters.ChatTabAdapters.ChatTabAdapter;
import fusionkey.lowkey.listAdapters.ChatTabAdapters.ChatTabViewHolder;
import fusionkey.lowkey.models.NotificationTO;
import fusionkey.lowkey.models.UserD;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    private ArrayList<NotificationTO> mNotifications;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationTO item);

    }

    public NotificationAdapter(ArrayList<NotificationTO> mNotifications) {
        this.mNotifications = mNotifications;

    }


    public void setListener(NotificationAdapter.OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationTO notification = this.mNotifications.get(position);

        // If the message is a received message.
        // Show received message in left linearlayout.
        holder.name.setText(notification.getUsername());
        holder.data.setText(notification.getMessage());

        holder.bind(mNotifications.get(position), listener);

        // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
        // Otherwise each iteview's distance is too big.


    }

    @Override
    public int getItemCount() {
        return mNotifications != null ? mNotifications.size() : 0;
    }
    public void deleteItem(int pos) {
        mNotifications.remove(pos);
        notifyItemRemoved(pos);
    }
}