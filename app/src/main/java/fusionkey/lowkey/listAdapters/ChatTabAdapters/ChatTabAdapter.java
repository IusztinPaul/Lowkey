package fusionkey.lowkey.listAdapters.ChatTabAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.models.UserD;


public class ChatTabAdapter extends RecyclerView.Adapter<ChatTabViewHolder> {

    private ArrayList<UserD> mUsers;
    private String last,date;
    private String state;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(UserD item);
        boolean onLongClick(UserD item, int pos);
    }

    private OnItemClickListener listener;

    public ChatTabAdapter(ArrayList<UserD> users, Context context) {
        mUsers = users;
        this.context = context;
    }

    public void swapDataSet(ArrayList<UserD> newData){
        mUsers.clear();
        mUsers.addAll(newData);
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public ChatTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatTabViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ChatTabViewHolder holder, int position) {
            UserD userDto = this.mUsers.get(position);
            UserDB userDB = new UserAttributeManager(userDto.getUserEmail()).getUserDB();

            try {
                holder.name.setText(userDB.getUsername());
            } catch (NullPointerException npe){
                holder.name.setText("Not found");
            }
            holder.lastmsg.setText(userDto.getLast_message());
            holder.bind(mUsers.get(position), listener);

            final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
            photoUploader.download(UserManager.parseEmailToPhotoFileName(userDto.getUserEmail()),
                    new Callback() {
                        @Override
                        public void handle() {
                            Log.e("PHOTO", "photo downloaded");
                            Picasso.with(context).load((photoUploader.getFileTO())).into(holder.image);
                        }
                    }, new Callback() {
                        @Override
                        public void handle() {
                            Picasso.with(context).load((R.drawable.avatar_placeholder)).into(holder.image);
                        }
                    });





    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }
    public void deleteItem(int pos) {
        mUsers.remove(pos);
        notifyItemRemoved(pos);
    }
}