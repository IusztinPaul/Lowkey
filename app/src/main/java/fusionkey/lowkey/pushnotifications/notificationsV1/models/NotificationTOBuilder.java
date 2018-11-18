package fusionkey.lowkey.pushnotifications.notificationsV1.models;

import java.io.File;

@Deprecated
public class NotificationTOBuilder {

    private String message;
    private String username;
    private String userID;
    private File file;
    private String timestamp;
    private boolean seen;


    public NotificationTOBuilder(String message, String username, String timestamp, String userID) {
        this.message = message;
        this.username = username;
        this.timestamp = timestamp;
        this.userID = userID;
    }

    public NotificationTOBuilder addPhotoFile(File file) {
        this.file = file;
        return this;
    }

    public NotificationTOBuilder addSeenState(boolean seen) {
        this.seen = seen;
        return this;
    }

    public NotificationTO build() {
        return new NotificationTO(
                message,
                username,
                timestamp,
                userID);
    }
}
