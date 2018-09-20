package fusionkey.lowkey.listAdapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;
import com.amazonaws.services.cognitoidentityprovider.model.UserType;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.auth.utils.UserAttributesEnum;
import fusionkey.lowkey.listAdapters.CommentAdapters.CommentAdapter;
import fusionkey.lowkey.listAdapters.CommentAdapters.CustomLinearLayoutManager;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.newsfeed.Comment;
import fusionkey.lowkey.newsfeed.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.NewsfeedRequest;

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

        /**
         * @TO-DO aici trebue sa incarci poza din S3 , asa :
         * holder.image.setImageBitmap(YOUR_FUNCTION);
         */

        holder.image.setImageResource(R.drawable.avatar_placeholder);
        final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
        photoUploader.download(msgDto.getId(),
                new Callback() {
                    @Override
                    public void handle() {
                        holder.image.setImageBitmap(photoUploader.getPhoto());
                    }
                }, null);

        if(!msgDto.getAnon()) {
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
        mMessages.clear();
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
