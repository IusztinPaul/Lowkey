package fusionkey.lowkey.listAdapters.NotificationAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;
import fusionkey.lowkey.pushnotifications.notificationsV1.models.NotificationTO;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.GroupNotificationAbstract;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout leftMsgLayout;
    TextView data;
    TextView name;
    CircleImageView pic;
    public NotificationViewHolder(View itemView) {
        super(itemView);

            name = (TextView) itemView.findViewById(R.id.username);
            data = (TextView) itemView.findViewById(R.id.data);
            pic = (CircleImageView) itemView.findViewById(R.id.profilePic);
            leftMsgLayout = itemView.findViewById(R.id.layout);
    }

    public void bind(final GroupNotificationAbstract item, final NotificationAdapter.OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });


    }
}
