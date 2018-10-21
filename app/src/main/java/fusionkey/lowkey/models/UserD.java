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
    private String username;

    @ColumnInfo(name = "messages")
    private ArrayList<MessageTO> listMessage;

    @ColumnInfo(name = "last")
    private String last_message;

    @ColumnInfo(name = "state")
    private String state;

    public UserD(String username, String last_message, ArrayList<MessageTO> listMessage,String state){
        this.setUsername(username);
        this.setLast_message(last_message);
        this.setListMessage(listMessage);
        this.setState(state);
    }

    public UserD(){}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
