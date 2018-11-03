package fusionkey.lowkey.chat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fusionkey.lowkey.chat.interfaces.Ichatroom;
import fusionkey.lowkey.chat.interfaces.VolleyResponseListener;

import static com.android.volley.Request.Method.GET;
import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 24-Aug-18
 *
 * <h1>This is the class that creates the Chat-Room</h1>
 *
 */

public class ChatRoom extends AbstractChat implements Ichatroom {

    public boolean isChatRoomAlive;

    public ChatRoom(String otherUser, String currentUser){
        super(otherUser,currentUser);
    }

    /**
     * <h1> POST METHOD FOR LAMBDA /CROOM/join</h1>
     * <p>  This method creates the chat room</p>
     *
     */
    public void joinRoom(){
        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getUser());
        queryParameters.put(LISTENER_API_QUERY_STRING,this.getListener());
        isChatRoomAlive=true;
        this.requestMethod("joinRoom","POST",queryParameters,CHATROOM_RELATIVE_URL);
    }
    /**
     * <h1> DELETE METHOD FOR LAMBDA /CROOM/join</h1>
     * <p>  This method deletes the chat room from the queue. The chatroom will be deleted by the end of a chat activity</p>
     *
     */
    public void deleteRoom(){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getUser());
        queryParameters.put(LISTENER_API_QUERY_STRING,this.getListener());
        isChatRoomAlive=false;
        this.requestMethod("joinRoom","DELETE",queryParameters,CHATROOM_RELATIVE_URL);
    }
    /**
     * <h1> POST METHOD FOR LAMBDA /CROOM/iswritting</h1>
     * <p>  This method set your writting-flag true </p>
     *
     */
    public void userIsWritting(){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getUser());
        queryParameters.put(LISTENER_API_QUERY_STRING,this.getListener());
        this.requestMethod("UserIsWritting","POST",queryParameters,ISWRITING_RELATIVE_URL);
    }
    /**
     * <h1> GET METHOD FOR LAMBDA /CROOM/iswritting</h1>
     * <p>  This method gets the user's writting-flag you're chatting</p>
     * <p1> VolleyResponseListener used for getting the response somewhere else</p1>
     */
    public void listenerIsWritting(final VolleyResponseListener listener){

        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getListener());
        queryParameters.put(LISTENER_API_QUERY_STRING,this.getUser());
        String URL = getAbsoluteUrlWithQueryString(queryParameters, ISWRITING_RELATIVE_URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, URL,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
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
     * <h1> DELETE METHOD FOR LAMBDA /CROOM/iswritting</h1>
     * <p>  This method set your writting-flag false</p>
     *
     */
    public void stopIsWritting(){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getUser());
        queryParameters.put(LISTENER_API_QUERY_STRING,this.getListener());
        this.requestMethod("stopIsWritting","DELETE",queryParameters,ISWRITING_RELATIVE_URL);
    }
    /**
     * <h1> GET METHOD FOR LAMBDA /CROOM/inChat</h1>
     * <p>  This method gets the user's conection-flag you're chatting with </p>
     * <p1> VolleyResponseListener used for getting the response somewhere else </p1>
     */
    public void getUserState(final VolleyResponseListener listener){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, this.getListener());
        String URL = getAbsoluteUrlWithQueryString(queryParameters, USER_RELATIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, URL,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
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
     * <h1> GET METHOD FOR LAMBDA /CROOM/message</h1>
     * <p>  This method gets the message in your ChatRoom you created </p>
     * <p1> VolleyResponseListener used for getting the response somewhere else </p1>
     *
     * <h1> WHY USED HERE ?</h1>
     * <p2> Felt more natural because it gets the message from your ChatRoom</p2>
     */
    public void getMsg(final VolleyResponseListener listener){

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, getListener());
        queryParameters.put(LISTENER_API_QUERY_STRING, getUser());


        String URL = getAbsoluteUrlWithQueryString(queryParameters, MESSAGE_RELATAIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, URL,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
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





