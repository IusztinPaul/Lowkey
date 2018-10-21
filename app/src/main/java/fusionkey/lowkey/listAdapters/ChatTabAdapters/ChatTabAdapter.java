package fusionkey.lowkey.listAdapters.ChatTabAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import fusionkey.lowkey.R;
import fusionkey.lowkey.models.UserD;


public class ChatTabAdapter extends RecyclerView.Adapter<ChatTabViewHolder> {

    private ArrayList<UserD> mUsers;
    private String last,date;
    private String state;


    public interface OnItemClickListener {
        void onItemClick(UserD item);
        boolean onLongClick(UserD item, int pos);
    }

    private OnItemClickListener listener;

    public ChatTabAdapter(ArrayList<UserD> users) {
        mUsers = users;

    }

    public void swapDataSet(ArrayList<UserD> newData){
        mUsers.clear();
        mUsers.addAll(newData);
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public ChatTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatTabViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatTabViewHolder holder, int position) {
             UserD userDto = this.mUsers.get(position);

            // If the message is a received message.
            // Show received message in left linearlayout.
            holder.name.setText(userDto.getUsername());
            holder.lastmsg.setText(userDto.getLast_message());
            holder.bind(mUsers.get(position), listener);

            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.


    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }
    public void deleteItem(int pos) {
        mUsers.remove(pos);
        notifyItemRemoved(pos);
    }
}