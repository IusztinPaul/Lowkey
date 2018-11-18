package fusionkey.lowkey.pushnotifications.notificationsV1.models;

import java.io.File;

@Deprecated
public class NotificationTO {
    private String message;
    private String userID;
    private String username;
    private File file;
    private String timestamp;
    private boolean seen;

    public NotificationTO(String message, String username, String timestamp, String userID) {
        this.message = message;
        this.username = username;
        this.setUserID(userID);
        this.setTimestamp(timestamp);
    }

    @Deprecated
    public String getMessage() {
        return message;
    }

    @Deprecated
    public void setMessage(String message) {
        this.message = message;
    }

    @Deprecated
    public String getUsername() {
        return username;
    }

    @Deprecated
    public void setUsername(String username) {
        this.username = username;
    }

    @Deprecated
    public File getFile() {
        return file;
    }

    @Deprecated
    public void setFile(File file) {
        this.file = file;
    }

    @Deprecated
    public String getTimestamp() {
        return timestamp;
    }

    @Deprecated
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Deprecated
    public boolean isSeen() {
        return seen;
    }

    @Deprecated
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Deprecated
    public String getUserID() {
        return userID;
    }

    @Deprecated
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
