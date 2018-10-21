package fusionkey.lowkey.chat.models;

import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgViewHolder;

public interface IContentViewCreator {
    void createView(ChatAppMsgViewHolder holder, MessageTO msg);
}
