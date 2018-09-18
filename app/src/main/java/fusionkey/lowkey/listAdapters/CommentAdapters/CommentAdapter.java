package fusionkey.lowkey.listAdapters.CommentAdapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.R;
import fusionkey.lowkey.listAdapters.ChatTabViewHolder;
import fusionkey.lowkey.newsfeed.Comment;
import fusionkey.lowkey.newsfeed.NewsFeedMessage;

public class CommentAdapter extends  RecyclerView.Adapter<CommentViewHolder>{

    ArrayList<Comment> commentList;


    public CommentAdapter(ArrayList<Comment> commentList){
        this.commentList=commentList;
    }
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
        Comment comDto = this.commentList.get(position);
        holder.date.setText(localTime(comDto.getCommentTStamp()));
        holder.answers.setText(comDto.getCommentTxt());
        holder.uniqueID=comDto.getCommentUserId();

    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    private String localTime(String time){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(Long.parseLong(time));
        return sf.format(date);
    }

}
