package fusionkey.lowkey.listAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.newsfeed.NewsFeedMessage;

public class NewsfeedAdapter extends RecyclerView.Adapter<ChatTabViewHolder> {

    private List<NewsFeedMessage> mMessages;
    private static String ANON_STRING = "Anonymous";
    private Context mcontext;

    public NewsfeedAdapter(List<NewsFeedMessage> mMessages,Context context){
        this.mMessages=mMessages;
        this.mcontext=context;
    }

    public interface OnItemClickListenerNews {
        void onItemClick(ChatTabViewHolder item,View v);
        boolean onLongClick(ChatTabViewHolder item, int pos);

    }

    @Override
    public ChatTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatTabViewHolder chatTabViewHolder =new ChatTabViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false));
        return  chatTabViewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatTabViewHolder holder, int position) {
        final NewsFeedMessage msgDto = this.mMessages.get(position);

        holder.title.setText(msgDto.getTitle());
        holder.image.setImageBitmap(msgDto.getUserPhoto());

        if(!msgDto.getAnon()) {

            holder.name.setText(msgDto.getUser());

            UserAttributeManager attributeManager = new UserAttributeManager(msgDto.getId());
            holder.name.setText(attributeManager.getUsername());

        }else{
            holder.name.setText(ANON_STRING);
        }

        holder.lastmsg.setText(msgDto.getContent());

        holder.date.setText(localTime(msgDto.getDate()));


        if(msgDto.getCommentArrayList()!=null) {
            holder.answers.setText(msgDto.getCommentArrayList().size() + " Answers");
        }else {
            holder.answers.setText("0 Answers");
        }

        holder.bind(holder, listener);
    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    public NewsfeedAdapter(ArrayList users) {
        mMessages = users;
    }

    public OnItemClickListenerNews listener;

    public void clear(){

        //TODO: @Sebi I think that we should keep the cached messages for optimization.
        // mMessages.clear();
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListenerNews listener){
        this.listener=listener;
    }

    private String localTime(String time){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMMM - HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(Long.parseLong(time));
        return sf.format(date);
    }

    public void removeItem(int position) {
        mMessages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMessages.size());
    }

    public NewsFeedMessage getMsg(int position){
        return mMessages.get(position);
    }

    public void addItem(NewsFeedMessage country) {
        mMessages.add(country);
        notifyItemInserted(mMessages.size());
    }
}
