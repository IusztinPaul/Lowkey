package fusionkey.lowkeyfinal.Models;

public class MessageTO {

    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";


    private String sender;
    private String receiver;
    private String content;
    private String date;
    private String msgType;

    //default
    public MessageTO(){}

    public MessageTO(String sender, String receiver, String content, String date, String msgType){
        this.setSender(sender);
        this.setReceiver(receiver);
        this.setContent(content);
        this.setDate(date);
        this.msgType=msgType;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return getDate() + " : " + getContent();
    }

}
