package fusionkey.lowkey.chat;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 25-Aug-2018
 * <p1> Main message class-NOT UI embedded </p1>
 *
 */
public class Message extends AbstractChat {

    private String from;
    private String to;
    private String message;
    private Timestamp time;
    private String is_photo;
    private String listener;

    public Message(String from,String to,String listener,String message,Timestamp time,String is_photo){
        this.setFrom(from);
        this.setTo(to);
        this.setL(listener);
        this.setMessage(message);
        this.setTime(time);
        this.setIs_photo(is_photo);
    }

    /**
     * <P1>GETTERS & SETTERS </P1>
     *
     */
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Timestamp getTime() {
        return time;
    }
    public void setTime(Timestamp time) {
        this.time = time;
    }
    public String getIs_photo() {
        return is_photo;
    }
    public void setIs_photo(String is_photo) {
        this.is_photo = is_photo;
    }
    public String getL() {
        return listener;
    }
    public void setL(String listener) {
        this.listener = listener;
    }

    /**
     * POST method for sending the msg to the another user in chat
     */

    public void sendMsg(){

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put(MessageFrom_QUERY_STRING,from);
        queryParameters.put(MessageTo_QUERY_STRING,to);
        queryParameters.put(LISTENER_API_QUERY_STRING, listener);
        queryParameters.put(MessageIsPhoto_QUERY_STRING, is_photo);
        //creating the body request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("message", this.getMessage());
        }catch (JSONException e) {
            Log.e("JSONException","message is NULL");
        }

        String URL = getAbsoluteUrlWithQueryString(queryParameters, MESSAGE_RELATAIVE_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, URL,jsonBody,

                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("sendMsg", response.toString());
                            try {
                                if (!response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA))
                                    Log.e("SendMsg"+"Error", "The response has no data");
                            } catch (JSONException e) {
                                Log.e("JSONException SendMsg", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", error.toString());
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
}
