package fusionkey.lowkey.chat.models;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fusionkey.lowkey.ROOMdatabase.MessagesActivity;
import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgViewHolder;

import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_RECEIVED;
import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_RECEIVED_LAST;
import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_SENT;

public class ContentViewCreatorPhoto extends ContentViewCreatorUtils implements IContentViewCreator {
    @Override
    public void createView(final ChatAppMsgViewHolder holder, MessageTO msg) {
        // If the message is a received message.
        if(MSG_TYPE_RECEIVED.equals(msg.getMsgType()))
        {

            if(ChatActivity.USERNAME!=null)
            holder.leftName.setText(ChatActivity.USERNAME);
            else if(MessagesActivity.USERNAME!=null)
                holder.leftName.setText(MessagesActivity.USERNAME);
            else
                holder.leftName.setText("Not found");

            populateView(holder.leftMsgLayout, holder.leftDate, msg);
            hideTextAndShowImage(holder.leftMsgTextView, holder.leftIv, (Bitmap) msg.getContent());
            makeLayoutsVisibilityGONE(holder.rightMsgLayout, holder.leftMsgLayoutLAST);
        }
        // If the message is a sent message.
        else if(MSG_TYPE_SENT.equals(msg.getMsgType()))
        {
            populateView(holder.rightMsgLayout, holder.rightDate, msg);
            hideTextAndShowImage(holder.rightMsgTextView, holder.rightIv, (Bitmap) msg.getContent());
            makeLayoutsVisibilityGONE(holder.leftMsgLayout, holder.leftMsgLayoutLAST);
        }
        else if(MSG_TYPE_RECEIVED_LAST.equals(msg.getMsgType()))
        {
            populateView(holder.leftMsgLayoutLAST, holder.lastLeftDate, msg);
            hideTextAndShowImage(holder.lastLeftMsg, holder.lastIv, (Bitmap) msg.getContent());
            makeLayoutsVisibilityGONE(holder.leftMsgLayout, holder.rightMsgLayout);
        }
    }

    private void hideTextAndShowImage(TextView textView, ImageView imageView, Bitmap bitmap) {
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
    }
}
