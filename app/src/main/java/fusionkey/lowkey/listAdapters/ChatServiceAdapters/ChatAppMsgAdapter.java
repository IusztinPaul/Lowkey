package fusionkey.lowkey.listAdapters.ChatServiceAdapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fusionkey.lowkey.R;
import fusionkey.lowkey.chat.models.MessageTO;

/**
 * Created by Jerry on 12/19/2017.
 */

public class ChatAppMsgAdapter extends RecyclerView.Adapter<ChatAppMsgViewHolder> {

    private List<MessageTO> msgDtoList = null;

    public ChatAppMsgAdapter(List<MessageTO> msgDtoList) {
        this.msgDtoList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        this.msgDtoList.get(position).createView(holder);
    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(msgDtoList==null)
        {
            msgDtoList = new ArrayList<MessageTO>();
        }
        return msgDtoList.size();
    }
}