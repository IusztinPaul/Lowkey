package fusionkey.lowkey.pushnotifications.models;

import java.io.File;

public class NotificationTO {
    private String message;
    private String username;
    private File file;
    private String timestamp;

    public NotificationTO(String message,String username,String timestamp){
        this.message=message;
        this.username=username;
        this.setTimestamp(timestamp);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
