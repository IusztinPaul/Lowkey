package fusionkey.lowkey.pushnotifications.notificationsV2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.pushnotifications.notificationsV2.models.Notification;

public class GroupingLowLevel {

    private static SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(LowKeyApplication.instance);
    private static String _id = LowKeyApplication.userManager.getCachedEmail();
    private static HashMap<String, ArrayList<Notification>> _hashMap = new HashMap<>();

    private static void groupTimestamps() {
        int counter = _preferences.getInt(_id, 0);

        /* Query for Notifications */
        for (int i = 0; i < counter; i++) {
            String[] s = _preferences.getString(_id + i, "").split("muiepsdasdfghjkl");
            /* Grouping by Timestamps */
            if (_hashMap.containsKey(s[1]))
                addToList(s[1], new Notification(
                        s[0].replace("{default=",""),
                        s[1].replace("}",""),
                        s[2],
                        s[3],
                        s[4].replace("}","")));
            else {
                _hashMap.put(s[1], null);
                addToList(s[1], new Notification(
                                    s[0].replace("{default=",""),
                                    s[1].replace("}",""),
                                    s[2],
                                    s[3],
                                    s[4].replace("}","")));
            }
        }
    }

    private static synchronized void addToList(String mapKey, Notification notificationTO) {
        ArrayList<Notification> items = _hashMap.get(mapKey);

        if (items == null) {
            items = new ArrayList<Notification>();
            items.add(notificationTO);
            _hashMap.put(mapKey, items);
        } else {
            items.add(notificationTO);
        }
    }

    public static HashMap<String,ArrayList<Notification>> getHashMap(){
        groupTimestamps();
        return _hashMap;
    }
}


