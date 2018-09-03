package fusionkey.lowkey.chat;

import android.app.VoiceInteractor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 24-Aug-18
 *
 * <h1>An Abstract class that contains the URL and some URL manipulation UTILS</h1>
 * <p> Also allows the creation of others forms of ChatRooms</p>
 *
 *
 */
abstract class AbstractChat {


    private static final String MAIN_API_URL = "https://cohag3cqv9.execute-api.eu-central-1.amazonaws.com/dev/";
    private static final String MAIN_CHAT_API_URL = MAIN_API_URL + "croom/";
    private static final String MAIN_USER_API_URL = MAIN_API_URL +"user/";
    static final String USER_RELATIVE_URL ="inchat/";

    static final String CHATROOM_RELATIVE_URL= "join/";
    static final String MESSAGE_RELATAIVE_URL="message/";
    static final String ISWRITING_RELATIVE_URL="iswriting/";

    static final String USER_API_QUERY_STRING = "user";
    static final String LISTENER_API_QUERY_STRING = "listener";
    static final String MessageFrom_QUERY_STRING = "from";
    static final String MessageTo_QUERY_STRING = "to";
    static final String MessagePageSize_QUERY_STRING = "size";
    static final String MessageIsPhoto_QUERY_STRING = "is_photo";
    static final String MessageBody_QUERY_STRING = "message";


    static final String RESPONSE_NO_DATA = "";
    static final String DATA_JSON_KEY = "data";

    public static final JSONObject JSON_FAILED_REQUESTED_OBJECT = new JSONObject();

    private String user;
    private String listener;

    AbstractChat(){}

    AbstractChat(String user, String listener){
        this.setUser(user);
        this.setListener(listener);
    }

    /**
     * QUERY UTILS
     */
    String getAbsoluteUrlWithQueryString(Map<?, ?> queryParameters, String relativeUrl) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : queryParameters.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }

        return getAbsoluteUrl(relativeUrl) + "?" + sb.toString();
    }
    private String getAbsoluteUrl(String relativeUrl) {
        return MAIN_CHAT_API_URL + relativeUrl;
    }
    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * GETTERS AND SETTERS
     *
     */
    String getUser() { return user; }
    String getListener() { return listener; }
    private void setUser(String user) { this.user = user; }
    private void setListener(String listener) { this.listener = listener; }

    /**
     * <h1> Universal requestMethod that maps with your needs</h1>
     *
     */
    void requestMethod(final String service,final String method, HashMap<String,String> queryParameters,String RELATIVE_URL){

        String URL = getAbsoluteUrlWithQueryString(queryParameters, RELATIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getRequestMethod(method), URL,null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(service, response.toString());
                        try {
                            if (response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA))
                                Log.e(method+":"+service+"->"+"Error", "The response has no data");
                        } catch (JSONException e) {
                            Log.e("JSONException", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(method+":"+service+"Error", error.toString());
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }

    /**
     *
     * @param method
     * @return the index of the method
     */
    private static int getRequestMethod(String method) {
        if (method != null) {
            if (method.equals("POST"))
                return Request.Method.POST;
            if (method.equals("GET"))
                return Request.Method.GET;
            if (method.equals("DELETE"))
                return Request.Method.DELETE;
        }
            return -1;
    }
}

