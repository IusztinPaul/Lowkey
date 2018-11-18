package fusionkey.lowkey.pushnotifications.notificationsV2.models;

public class NotificationY extends GroupNotificationAbstract {

    public NotificationY(String timestamp,int counter, Notification notification){
        super(timestamp,counter,notification);
    }

    public String mapTheString(){
        return super.getNotification().getUsername() + " and other " + super.getCounter() + " people anwered your "+
                "question : " + super.getNotification().getPtittle();
    }

}
