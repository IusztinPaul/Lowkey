package fusionkey.lowkey.models;

import java.io.File;

public class NotificationTO {
    private String message;
    private String username;
    private File file;

    public NotificationTO(String message,String username){
        this.message=message;
        this.username=username;
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
}
