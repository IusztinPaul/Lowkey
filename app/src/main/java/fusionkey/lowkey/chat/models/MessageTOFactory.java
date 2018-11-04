package fusionkey.lowkey.chat.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fusionkey.lowkey.main.utils.PhotoUploader;

public class MessageTOFactory {
    private String sender;
    private String receiver;
    private long timestamp;
    private String content;
    private boolean is_photo = false;
    private String msgType;

    public MessageTOFactory(JSONObject data, String msgType) {
        try {
            this.sender = data.getString("from");
        } catch (JSONException e) {
            Log.e("MessageTOFactory", e.getMessage());
        }

        try {
            this.receiver = data.getString("to");
        } catch (JSONException e) {
            Log.e("MessageTOFactory", e.getMessage());
        }

        try {
            this.timestamp = data.getLong("sent_timestamp");

        } catch (JSONException e) {
            Log.e("MessageTOFactory", e.getMessage());
        }

        try {
            this.content = data.getString("message");
        } catch (JSONException e) {
            Log.e("MessageTOFactory", e.getMessage());
        }

        try {
            this.is_photo = data.getBoolean("is_photo");
        } catch (JSONException e) {
            Log.e("MessageTOFactory", e.getMessage());
        }

        this.msgType = msgType;
    }

    public MessageTOFactory(String sender, String receiver, long timestamp, String content, boolean is_photo, String msgType) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.content = content;
        this.is_photo = is_photo;
        this.msgType = msgType;
    }

    public MessageTOFactory(String sender, String receiver, long timestamp, Bitmap content, boolean is_photo, String msgType) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.content =  new PhotoUploader.BitMapOperator(content).serializeToString();
        this.is_photo = is_photo;
        this.msgType = msgType;
    }

    public MessageTO createMessage() {
        String formattedDate = getFormattedDate();
        return new MessageTO(sender, receiver, formattedDate, msgType, content, is_photo);
    }


    private String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        sf.setTimeZone(tz);
        Date date = new Date(timestamp);
        return sf.format(date);
    }

}
