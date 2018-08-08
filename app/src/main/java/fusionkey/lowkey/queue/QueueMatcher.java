package fusionkey.lowkey.queue;

import android.app.Activity;
import android.util.Log;

import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

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

/**
 * @author Iusztin Paul
 * @version 1.0
 * @since 31.07.2018
 *
 * <h1>Class that wraps the Queue API hosted in AWS</h1>
 * <h2>It uses lambda functions and Redis ElastiCache as backend</h2>
 *
 * <p>The requests are make with Volley</p>
 */

public class QueueMatcher implements IQueueMatcher {

    static final String MAIN_API_URL = "https://gra4ddmrz6.execute-api.eu-central-1.amazonaws.com/dev/";
    static final String MAIN_QUEUE_API_URL = MAIN_API_URL + "queue/";

    static final String LISTENER_RELATIVE_URL = "listener/";
    static final String SEAPKER_RELATIVE_URL = "speaker/";

    public static final String DATA_JSON_KEY = "data";
    public static final String ERROR_JSON_LEY = "errorMessage";
    public static final String STATUS_CODE_JSON_KEY = "statusCode";
    public static final String DELETE_INFO_JSON_KEY = "deleteInfoMessage";
    public static final String RESPONSE_NO_DATA = "";

    static final String USER_API_QUERY_STRING = "user";
    static final String LISTENER_API_QUERY_STRING = "listener";

    public static final JSONObject JSON_FAILED_REQUESTED_OBJECT = new JSONObject();

    private String currentUser;
    private Activity currentActivity;

    private LobbyCheckerRunnable findSpeakerRunnable;
    private LobbyCheckerRunnable findListenerRunnable;

    public QueueMatcher(String currentUser, Activity currentActivity) {
        this.currentUser = currentUser;
        this.currentActivity = currentActivity;
    }

    /**
     * This method first makes a POST to add the listener to the queue and create a new lobby.
     * After it waits for speakers to come to the lobby checking with a GET method in the aws cache.
     * When the lobby it's full the speakers are returned in a container hosted by the
     * LobbyCheckerRunnable class that wraps the request and response.
     */
    @Override
    public void findSpeakers() { // as a listener

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, currentUser);
        String url = getAbsoluteUrlWithQueryString(queryParameters, LISTENER_RELATIVE_URL);

        findSpeakerRunnable = new LobbyCheckerRunnable(url, currentUser, null);

        // Call L0 lambda function
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("findSpeakers", response.toString());

                        try {
                            // continue only of the response has data
                            if(!response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA))
                                currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Thread(findSpeakerRunnable).start();
                                    }
                                });
                        } catch (JSONException e) {
                            Log.e("JSONException", "Json response had no '" + DATA_JSON_KEY +  "' key");
                            if(findSpeakerRunnable != null)
                                findSpeakerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("findSpeakerError", error.toString());
                        if(findSpeakerRunnable != null) {
                            findSpeakerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
                            findSpeakerRunnable.setStillChecking(false);
                        }
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
     * This method firstly POST the speaker in a queue and binds to a listeners queue.
     * After it checks with a GET method if the lobby is full. When the lobby is full it
     * returns all the speakers from the lobby. The listener, currentSpeaker, request and response
     * are wrapped withing the LobbyCheckerRunnable class.
     */
    @Override
    public void findListener() { // as a speaker
        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, currentUser);
        String url = getAbsoluteUrlWithQueryString(queryParameters, SEAPKER_RELATIVE_URL);

        // call S0 lambda function
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("findListener", response.toString());

                        try {
                            // continue only if the response has data
                            if(!response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA)) {
                                final String listener = response.get(DATA_JSON_KEY).toString();

                                HashMap<String, String> queryParameters = new HashMap<>();
                                queryParameters.put(USER_API_QUERY_STRING, currentUser);
                                queryParameters.put(LISTENER_API_QUERY_STRING, listener);
                                final String url = getAbsoluteUrlWithQueryString(queryParameters, SEAPKER_RELATIVE_URL);

                                currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findListenerRunnable = new LobbyCheckerRunnable(url, listener, currentUser);
                                        new Thread(findListenerRunnable).start();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Log.e("JSONException", "Json response had no '" + DATA_JSON_KEY +  "' key");
                            if(findListenerRunnable != null) {
                                findListenerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
                                findListenerRunnable.setStillChecking(false);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("findSpeakerError", error.toString());
                        if(findListenerRunnable != null) {
                            findListenerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
                            findListenerRunnable.setStillChecking(false);
                        }
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
     * This method gets the speaker out of the current lobby. If the speaker it's not in any lobby
     * nothing will happen.
     */
    @Override
    public synchronized void stopFindingSpeaker() { // as listener
        if(findSpeakerRunnable != null)
            if(findSpeakerRunnable.isStillChecking())
                findSpeakerRunnable.setStillChecking(false);
            else
                findSpeakerRunnable.makeListenerDeleteRequest();
    }

    /**
     * This method removes the lobby of the listener and ads a flag that the listener from the queue
     * it's deprecated. So no more lobbies will be created for that listener.
     */
    @Override
    public synchronized void stopFindingListener() { // as speaker
        if(findListenerRunnable != null)
            if(findListenerRunnable.isStillChecking())
                findListenerRunnable.setStillChecking(false);
            else
                findListenerRunnable.makeSpeakerDeleteRequest();
    }

    /**
     * @return the container generated by the findSpeakers() method. It returns null if there is
     * no container.
     */
    @Override
    public JSONObject getSpeakers() {
        try {
            if(findSpeakerRunnable == null || findSpeakerRunnable.isStillChecking())
                return JSON_FAILED_REQUESTED_OBJECT;

            return findSpeakerRunnable.getResponseContainer();
        } finally {
            // after you get the speaker we put it on null so the data is consistent
            // you will know if there is new data or not at a further call of findSpeaker
            if(findSpeakerRunnable != null)
                findSpeakerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
        }
    }

    /**
     * @return the container generated by the findListener() method. It returns null if there is
     * no container.
     */
    @Override
    public JSONObject getListener() {
        try {
            if(findListenerRunnable == null || findListenerRunnable.isStillChecking())
                return JSON_FAILED_REQUESTED_OBJECT;

            return findListenerRunnable.getResponseContainer();
        } finally {
            // after you get the speaker we put it on null so the data is consistent
            // you will know if there is new data or not at a further call of findListener
            if(findListenerRunnable != null)
                findListenerRunnable.setResponseContainer(JSON_FAILED_REQUESTED_OBJECT);
        }
    }

    public boolean isLoopCheckerAliveListener() {
        return findListenerRunnable != null && findListenerRunnable.isStillChecking();
    }

    public boolean isLoopCheckerAliveSpeaker() {
        return findSpeakerRunnable != null && findSpeakerRunnable.isStillChecking();
    }

    public int getLoopStateListener() {
        if(findListenerRunnable == null)
            return 0;

        return findListenerRunnable.getLoopState();
    }

    public int getLoopStateSpeaker() {
        if(findSpeakerRunnable == null)
            return 0;

        return findSpeakerRunnable.getLoopState();
    }

    private String getAbsoluteUrlWithQueryString(Map<?, ?> queryParameters, String relativeUrl) {
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
        return MAIN_QUEUE_API_URL + relativeUrl;
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
