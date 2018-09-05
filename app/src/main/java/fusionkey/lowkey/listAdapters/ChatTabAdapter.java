package fusionkey.lowkey.listAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


import fusionkey.lowkey.R;
import fusionkey.lowkey.models.NewsFeedMessage;

public class ChatTabAdapter extends RecyclerView.Adapter<ChatTabViewHolder> {

    private List<NewsFeedMessage> mMessages;
    private String last,date;




    public ChatTabAdapter(ArrayList users) {
        mMessages = users;

    }

    public void swapDataSet(List<NewsFeedMessage> newData){
        mMessages.clear();
        mMessages.addAll(newData);
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }

    @Override
    public ChatTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatTabViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatTabViewHolder holder, int position) {
        NewsFeedMessage msgDto = this.mMessages.get(position);

        // If the message is a received message.
        // Show received message in left linearlayout.
        holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
        holder.name.setText(msgDto.getUser());
        holder.lastmsg.setText(msgDto.getContent());
        holder.date.setText(msgDto.getDate());
        holder.answers.setText(msgDto.getAnswers());

        // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
        // Otherwise each iteview's distance is too big.


    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }
    public void removeItem(int position) {
        mMessages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMessages.size());
    }
    public void addItem(NewsFeedMessage country) {
        mMessages.add(country);
        notifyItemInserted(mMessages.size());
    }
}
