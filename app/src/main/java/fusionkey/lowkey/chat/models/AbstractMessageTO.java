package fusionkey.lowkey.chat.models;

public abstract class AbstractMessageTO {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    public final static String MSG_TYPE_RECEIVED_LAST = "MSG_TYPE_RECEIVED_LAST";

    protected String sender;
    protected String receiver;
    protected String date;
    protected String msgType;

    public AbstractMessageTO() {}

    public AbstractMessageTO(String sender, String receiver, String date, String msgType) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.msgType = msgType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return getDate() + " : ";
    }
}
