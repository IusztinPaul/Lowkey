package fusionkey.lowkey.chat.models;

import android.graphics.Bitmap;

import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgViewHolder;
import fusionkey.lowkey.main.utils.PhotoUploader;
import fusionkey.lowkey.main.utils.PhotoUtils;

public class MessageTO extends AbstractMessageTO {
    private static final int PHOTO = 1;
    private static final int TEXT = 2;

    private String content;
    private boolean is_photo;

    //default
    public MessageTO() {}

    MessageTO(String sender,
                     String receiver,
                     String date,
                     String msgType,
                     String content,
                     boolean is_photo) {
        super(sender, receiver, date, msgType);
        this.content = content;
        this.is_photo = is_photo;
    }

    private int getContentType() {
        if (is_photo)
            return PHOTO;
        else
            return TEXT;
    }

    public Object getContent() {
        switch (getContentType()) {
            case PHOTO:
                PhotoUploader.BitMapOperator operator = new PhotoUploader.BitMapOperator();
                Bitmap photo = operator.unserialize(content);
                return PhotoUtils.rotateBitmap90DegreesIfWidthBigger(photo);
            case TEXT:
                return content;
            default:
                return null;
        }
    }

    public void createView(ChatAppMsgViewHolder holder) {
        switch (getContentType()) {
            case PHOTO:
                new ContentViewCreatorPhoto().createView(holder, this);
                break;
            case TEXT:
                new ContentViewCreatorText().createView(holder, this);
                break;
            default:
        }
    }
}
