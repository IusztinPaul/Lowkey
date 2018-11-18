package fusionkey.lowkey.main.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonExtractor {

    private JSONObject data;

    public JsonExtractor(JSONObject data) {
        this.data = data;
    }

    public String extractString(String key) {
        try {
            return data.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public Boolean extractBoolean(String key) {
        try {
            return data.getBoolean(key);
        } catch (JSONException e) {
            return false;
        }
    }

    public Integer extractInteger(String key) {
        try {
            return data.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public Long extractLong(String key) {
        try {
            return data.getLong(key);
        } catch (JSONException e) {
            return 0L;
        }
    }
}
