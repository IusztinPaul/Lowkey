package fusionkey.lowkey.pushnotifications.notificationsV2.models;

import java.io.File;

public abstract class GroupNotificationAbstract {

    private String timestamp;
    private int counter;
    private File file;
    private Notification notification;

    public abstract String mapTheString();

    public GroupNotificationAbstract(String timestmap, int counter, Notification notification) {
        this.setTimestamp(timestmap);
        this.setCounter(counter);
        this.setNotification(notification);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
