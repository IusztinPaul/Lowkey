package fusionkey.lowkey.listAdapters.ChatServiceAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fusionkey.lowkey.R;


/**
 * Created by Jerry on 12/20/2017.
 */

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout leftMsgLayout;
    public ConstraintLayout leftMsgLayoutLAST;
    public ConstraintLayout rightMsgLayout;

    public TextView leftMsgTextView;
    public TextView leftName;
    public TextView leftDate;
    public ImageView leftIv;
    public TextView lastLeftMsg;
    public TextView lastLeftDate;
    public ImageView lastIv;
    public TextView rightDate;
    public TextView rightMsgTextView;
    public ImageView rightIv;

    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgLayoutLAST = (ConstraintLayout) itemView.findViewById(R.id.chat_left_msg_layout_LAST);

            leftName = (TextView) itemView.findViewById(R.id.text_message_name);

            leftDate = (TextView) itemView.findViewById(R.id.text_message_timeLeft);
            rightDate = (TextView) itemView.findViewById(R.id.text_message_timeRight);

            leftIv = (ImageView) itemView.findViewById(R.id.chat_left_iv);
            rightIv = (ImageView) itemView.findViewById(R.id.chat_right_iv);

            lastLeftDate = (TextView) itemView.findViewById(R.id.lastdate);
            lastLeftMsg = (TextView) itemView.findViewById(R.id.lastmsg);
            lastIv = (ImageView) itemView.findViewById(R.id.chat_last_iv);

            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
        }
    }
}