package fusionkey.lowkey.chat.interfaces;

import org.json.JSONObject;

/**
 * <p1>With this interface you can get the JSON response from GET methods whenever you want</p1>
 */
public interface VolleyResponseListener {
    void onError(String message);

    void onResponse(JSONObject response);
}