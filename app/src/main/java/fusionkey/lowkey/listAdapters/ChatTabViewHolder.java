package fusionkey.lowkey.listAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;

public class ChatTabViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView title;
    TextView name;
    TextView lastmsg;
    TextView date;
    TextView answers;
    CircleImageView image;
    public View view;
    //ImageView image;

    public ChatTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat);
            name = (TextView) itemView.findViewById(R.id.name);
            lastmsg = (TextView) itemView.findViewById(R.id.lastmsg);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.Title);
            answers = (TextView) itemView.findViewById(R.id.answers);
            image = (CircleImageView) itemView.findViewById(R.id.circleImageView);
            view = (View) itemView.findViewById(R.id.expand);
            view.setVisibility(View.INVISIBLE);

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

}
