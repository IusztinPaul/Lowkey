package fusionkey.lowkey.listAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fusionkey.lowkey.R;

public class ChatTabViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;

    TextView name;
    TextView lastmsg;
    TextView date;
    TextView answers;
    //ImageView image;

    public ChatTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (ConstraintLayout) itemView.findViewById(R.id.chat);
            name = (TextView) itemView.findViewById(R.id.name);
            lastmsg = (TextView) itemView.findViewById(R.id.lastmsg);
            date = (TextView) itemView.findViewById(R.id.date);
            answers = (TextView) itemView.findViewById(R.id.answers);
            // image = (ImageView) itemView.findViewById(R.id.image);

        }
    }


}
