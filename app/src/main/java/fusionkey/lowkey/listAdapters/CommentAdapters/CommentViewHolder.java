package fusionkey.lowkey.listAdapters.CommentAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView date;
    TextView name;
    TextView answers;
    String uniqueID;
    public View view;

    public CommentViewHolder(View itemView) {
        super(itemView);
        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            answers = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            date = (TextView) itemView.findViewById(R.id.text_message_timeLeft);
            name = (TextView) itemView.findViewById(R.id.username);
        }
    }

}
