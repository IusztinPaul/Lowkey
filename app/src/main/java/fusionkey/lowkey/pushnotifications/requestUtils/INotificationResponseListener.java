package fusionkey.lowkey.pushnotifications.requestUtils;

import org.json.JSONObject;

public interface INotificationResponseListener {
    void onError(String message);

    void onResponse(JSONObject response);
}
