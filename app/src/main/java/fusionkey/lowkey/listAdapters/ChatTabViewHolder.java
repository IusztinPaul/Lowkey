package fusionkey.lowkey.listAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;
import fusionkey.lowkey.newsfeed.models.Comment;

public class ChatTabViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView title;
    TextView name;
    TextView type;
    TextView lastmsg;
    TextView date;
    TextView answers;
    CircleImageView image;
    CardView normal,otherQ,yourQ;
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
            type = (TextView) itemView.findViewById(R.id.delete);

        }
    }
    public void bind(final ChatTabViewHolder item, final NewsfeedAdapter.OnItemClickListenerNews listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item,v);
            }
        });
    }
    public void bindDelete(final ChatTabViewHolder item, final NewsfeedAdapter.OnDeleteItem listener){
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.deleteItem(item,view);
            }
        });
    }

}


