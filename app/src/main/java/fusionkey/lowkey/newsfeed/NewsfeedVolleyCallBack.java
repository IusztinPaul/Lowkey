package fusionkey.lowkey.newsfeed;

import org.json.JSONObject;

public interface NewsfeedVolleyCallBack {
    void onError(String message);

    void onResponse(JSONObject response);
}
