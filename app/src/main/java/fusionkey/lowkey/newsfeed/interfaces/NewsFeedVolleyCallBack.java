package fusionkey.lowkey.newsfeed.interfaces;

import org.json.JSONObject;

public interface NewsFeedVolleyCallBack {
    void onError(String message);

    void onResponse(JSONObject response);
}
