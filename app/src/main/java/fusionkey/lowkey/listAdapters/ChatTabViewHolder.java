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
    CircleImageView image;
    public View view;
    private ArrayList<Comment> commentArrayList;
    //ImageView image;

    public ChatTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            name = (TextView) itemView.findViewById(R.id.name);
            lastmsg = (TextView) itemView.findViewById(R.id.lastmsg);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.Title);
            answers = (TextView) itemView.findViewById(R.id.answers);
            image = (CircleImageView) itemView.findViewById(R.id.circleImageView);
//            view.setVisibility(View.INVISIBLE);


        }
    }
    public void bind(final ChatTabViewHolder item, final NewsfeedAdapter.OnItemClickListenerNews listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item,v);
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

}
