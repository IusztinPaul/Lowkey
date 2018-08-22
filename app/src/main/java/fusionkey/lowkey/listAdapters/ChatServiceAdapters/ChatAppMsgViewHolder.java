package fusionkey.lowkey.listAdapters.ChatServiceAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fusionkey.lowkey.R;


/**
 * Created by Jerry on 12/20/2017.
 */

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;

    ConstraintLayout rightMsgLayout;

    TextView leftMsgTextView;
    TextView leftName;
    TextView leftDate;
    TextView rightDate;
    TextView rightMsgTextView;

    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            leftName = (TextView) itemView.findViewById(R.id.text_message_name);
            leftDate = (TextView) itemView.findViewById(R.id.text_message_timeLeft);
            rightDate = (TextView) itemView.findViewById(R.id.text_message_timeRight);
            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
        }
    }
}