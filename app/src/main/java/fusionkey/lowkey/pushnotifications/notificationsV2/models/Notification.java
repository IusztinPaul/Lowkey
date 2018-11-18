package fusionkey.lowkey.pushnotifications.notificationsV2.models;

public class Notification {
    private String username;
    private String timestamp;
    private String comment;
    private String ptittle;
    private String userID;

    public Notification(String username,
                        String timestamp,
                        String comment,
                        String ptittle,
                        String userID) {

        this.setComment(comment);
        this.setPtittle(ptittle);
        this.setTimestamp(timestamp);
        this.setUserID(userID);
        this.setUsername(username);

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPtittle() {
        return ptittle;
    }

    public void setPtittle(String ptittle) {
        this.ptittle = ptittle;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
