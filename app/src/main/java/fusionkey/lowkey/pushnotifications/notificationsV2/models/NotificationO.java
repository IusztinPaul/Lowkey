package fusionkey.lowkey.pushnotifications.notificationsV2.models;

public class NotificationO extends GroupNotificationAbstract {

    public NotificationO(String timestamp, int counter, Notification notification) {
        super(timestamp, counter, notification);
    }

    public String mapTheString() {
        return super.getNotification().getUsername() + " and other " + super.getCounter() + " people commented on a question " +
                "after you : " + super.getNotification().getPtittle();
    }
}