package fusionkey.lowkey.listAdapters.CommentAdapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.R;
import fusionkey.lowkey.newsfeed.models.Comment;

public class CommentAdapter extends  RecyclerView.Adapter<CommentViewHolder>{

    private List<Comment> commentList;
    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    private static String ANON_STRING = "Anonymous";


    public CommentAdapter(List<Comment> commentList, Context context){
        this.setCommentList(commentList);
        this.context=context;
    }
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);

        holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);

        Comment comDto = this.getCommentList().get(position);


        if(comDto.getCommentIsAnonymous().equalsIgnoreCase("true")) {
            holder.name.setText(comDto.getCommentUserUsername());
        }else{
            holder.name.setText(ANON_STRING);
        }
        holder.date.setText(localTime(comDto.getCommentTStamp()));

        holder.answers.setText(comDto.getCommentTxt());

        holder.uniqueID=comDto.getCommentUserId();

    }

    public void addItem() {
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }


    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }



    @Override
    public int getItemCount() {
        return getCommentList() != null ? getCommentList().size() : 0;
    }

    private String localTime(String time){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMMM - HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(Long.parseLong(time));
        PrettyTime t = new PrettyTime(date);
        return t.format(new Date());
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
