package fusionkey.lowkey.listAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;
import fusionkey.lowkey.newsfeed.Comment;

public class ChatTabViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView title;
    TextView name;
    TextView lastmsg;
    TextView date;
    TextView answers;
    Button send;
    EditText input;
    RecyclerView recyclerView;
    CircleImageView image;
    public View view;
    private ArrayList<Comment> commentArrayList;
    NestedScrollView nestedScrollView;
    //ImageView image;

    public ChatTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat);
            name = (TextView) itemView.findViewById(R.id.name);
            lastmsg = (TextView) itemView.findViewById(R.id.lastmsg);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.Title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
            answers = (TextView) itemView.findViewById(R.id.answers);
            image = (CircleImageView) itemView.findViewById(R.id.circleImageView);
            view = (View) itemView.findViewById(R.id.expand);
            nestedScrollView = itemView.findViewById(R.id.scroll);
            view.setVisibility(View.INVISIBLE);
            input = itemView.findViewById(R.id.chat_input_msg);
            send = itemView.findViewById(R.id.sendComment);

        }
    }
    public void bind(final ChatTabViewHolder item, final NewsfeedAdapter.OnItemClickListenerNews listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(item,getAdapterPosition());
                return true;// returning true instead of false, works for me
            }
        });
    }


    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }
}
