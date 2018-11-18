package fusionkey.lowkey.pushnotifications.notificationsV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.Notification;

public class GroupingMiddleLevel {

    /* YOURS_TAG Means it's a label for notifications for your Questions */
    private static String YOURS_TAG = "a";
    /* YOURS_TAG Means it's a label for notifications for people that commented after you */
    private static String OTHERS_TAG = "b";

    private static HashMap<String, ArrayList<Notification>> _lowLevelHashMap = GroupingLowLevel.getHashMap();

    private static HashMap<String, ArrayList<Notification>> _middleLevelHashMap = new HashMap<>();

    private static List<Long> timestamps = LowKeyApplication.userManager.getUserDetails().getTimeStamps();


    private static void labelNotifications() {
        _middleLevelHashMap.put(YOURS_TAG, null);
        _middleLevelHashMap.put(OTHERS_TAG, null);

        for (Long l : timestamps) {
            if (_lowLevelHashMap.containsKey(String.valueOf(l)))
                /* That means its labeled YOURS */
                addListToList(YOURS_TAG, _lowLevelHashMap.get(String.valueOf(l)));
            else
                addListToList(OTHERS_TAG, _lowLevelHashMap.get(String.valueOf(l)));

        }
    }

    private static synchronized void addListToList(String mapKey, ArrayList<Notification> arrayList) {
        ArrayList<Notification> items = _middleLevelHashMap.get(mapKey);

        if (items == null) {
            items = new ArrayList<Notification>();
            items.addAll(arrayList);
            _middleLevelHashMap.put(mapKey, items);
        } else {
            items.addAll(arrayList);
        }
    }

    public static HashMap<String,ArrayList<Notification>> getMiddleLevelHashMap(){
        labelNotifications();
        return _middleLevelHashMap;
    }

}
