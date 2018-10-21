package fusionkey.lowkey.listAdapters.ChatTabAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fusionkey.lowkey.R;
import fusionkey.lowkey.models.UserD;


public class ChatTabViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView title;
    TextView name;
    TextView lastmsg;
    ImageView imageStateHelped;
    ImageView imageStateGetHelp;

    public ChatTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {

            name = (TextView) itemView.findViewById(R.id.name);
            lastmsg = (TextView) itemView.findViewById(R.id.lastmsg);

        }
    }

    public void bind(final UserD item, final ChatTabAdapter.OnItemClickListener listener) {

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
