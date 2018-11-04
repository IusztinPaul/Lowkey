package fusionkey.lowkey.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

import fusionkey.lowkey.chat.models.MessageTO;

@Entity(tableName = "user")
public class UserD {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String userEmail;

    @ColumnInfo(name = "messages")
    private ArrayList<MessageTO> listMessage;

    @ColumnInfo(name = "last")
    private String last_message;

    @ColumnInfo(name = "state")
    private String state;

    public UserD(String userEmail,
                 ArrayList<MessageTO> listMessage,
                 String state) {
        this.setUserEmail(userEmail);
        this.setListMessage(listMessage);
        this.setLast_message(getLastMessage(listMessage));
        this.setState(state);
    }

    public UserD() {}

    public String getEmail() {
        return userEmail;
    }

    public void setEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private String getLastMessage(ArrayList<MessageTO> arrayList){
        if((arrayList.get(arrayList.size()-1).getContentType()) == MessageTO.PHOTO)
             return "user sent a photo";
        else
            return arrayList.get(arrayList.size()-1).getRawContent();
    }

    public ArrayList<MessageTO> getListMessage() {
        return listMessage;
    }

    public void setListMessage(ArrayList<MessageTO> listMessage) {
        this.listMessage = listMessage;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public void addMessages(ArrayList<MessageTO> listMessage){
        this.listMessage.addAll(listMessage);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
