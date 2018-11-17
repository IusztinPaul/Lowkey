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
            return (String) extract(JsonTypes.STRING, key);
        } catch (JSONException e) {
            return "";
        }
    }

    public Boolean extractBoolean(String key) {
        try {
            return (Boolean) extract(JsonTypes.BOOLEAN, key);
        } catch (JSONException e) {
            return false;
        }
    }

    public Integer extractInteger(String key) {
        try {
            return (Integer) extract(JsonTypes.INTEGER, key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public Long extractLong(String key) {
        try {
            return (Long) extract(JsonTypes.LONG, key);
        } catch (JSONException e) {
            return 0L;
        }
    }

    private Object extract(JsonTypes type, String key) throws JSONException{
            if (type.equals(JsonTypes.STRING))
                return data.getString(key);
            else if (type.equals(JsonTypes.BOOLEAN))
                return data.getBoolean(key);
            else if(type.equals(JsonTypes.INTEGER))
                return data.getInt(key);
            else if(type.equals(JsonTypes.LONG))
                return data.getLong(key);

        throw new JSONException("Not supported data");
    }



    private enum JsonTypes {
        STRING("string"),
        BOOLEAN("bool"),
        INTEGER("integer"),
        LONG("long")
        ;

        private String attribute;

        JsonTypes(String attribute) {
            this.attribute = attribute;
        }


        @Override
        public String toString() {
            return super.toString();
        }
    }
}
