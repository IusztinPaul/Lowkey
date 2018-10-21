package fusionkey.lowkey.chat.models;

import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgViewHolder;

import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_RECEIVED;
import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_RECEIVED_LAST;
import static fusionkey.lowkey.chat.models.MessageTO.MSG_TYPE_SENT;


public class ContentViewCreatorText extends ContentViewCreatorUtils implements IContentViewCreator {
    @Override
    public void createView(ChatAppMsgViewHolder holder, MessageTO msg) {
        if(MSG_TYPE_RECEIVED.equals(msg.getMsgType()))
        {
            holder.leftName.setText(msg.getSender());
            populateView(holder.leftMsgLayout, holder.leftMsgTextView, holder.leftDate, msg);
            makeLayoutsVisibilityGONE(holder.rightMsgLayout, holder.leftMsgLayoutLAST, holder.leftIv);
        }
        else if(MSG_TYPE_SENT.equals(msg.getMsgType()))
        {
            populateView(holder.rightMsgLayout, holder.rightMsgTextView, holder.rightDate, msg);
            makeLayoutsVisibilityGONE(holder.leftMsgLayout, holder.leftMsgLayoutLAST, holder.rightIv);
        }
        else if(MSG_TYPE_RECEIVED_LAST.equals(msg.getMsgType()))
        {
            populateView(holder.leftMsgLayoutLAST, holder.lastLeftMsg, holder.lastLeftDate, msg);
            makeLayoutsVisibilityGONE(holder.leftMsgLayout, holder.rightMsgLayout, holder.lastIv);
        }
    }
}
