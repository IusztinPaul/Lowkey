package fusionkey.lowkey.queue;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;


public class LobbyCheckerRunnable implements Runnable {

    public static final int TIME_LOOPING_MILLISECONDS = 30 * 1000;

    private String requestUrl;
    private String listener;
    private String callerSpeaker;
    private JSONObject usefullResponseContainer;
    private boolean stillChecking = false;
    private int i = TIME_LOOPING_MILLISECONDS;
    private JSONObject responseContainer = QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;


    LobbyCheckerRunnable(String requestUrl, String listener, String callerSpeaker) {
        this.requestUrl = requestUrl;
        this.listener = listener;
        this.callerSpeaker = callerSpeaker;
    }

    /**
     *
     * @author Sandru sebastian
     * <p1> Am mai facut inca un constructor pentru LobbyCheckerRunnable ca sa pasez response-ul din
     * FindListener </p1>
     * <p2> Motivul pentru care am facut asta a fost ca responseul pe care LobbyCheckerRunnable nu imi era folositor
     * pentru a mapa Queue-ul cu chatul :) </p2>
     *
     *
     */
    LobbyCheckerRunnable(String requestUrl, String listener, String callerSpeaker, JSONObject usefullResponseContainer) {
        this.requestUrl = requestUrl;
        this.listener = listener;
        this.callerSpeaker = callerSpeaker;
        this.usefullResponseContainer = usefullResponseContainer;
    }

    @Override
    public void run() {
        i = TIME_LOOPING_MILLISECONDS;
        boolean dataFound = false;
        stillChecking = true;

        try {
        while (stillChecking && i > 0) {

            responseContainer = null;
            requestQueueSingleton.addToRequestQueue(getRequest());

            while (responseContainer == null) {
                Thread.sleep(2);
                i -= 2;

                if(i <= 0) {
                    i = 0;
                    break;
                }
            }

            // If the listener exits the lobby all the speakers waiting for the listener
            // have to be thrown out -> containers will be filled with JSON_FAILED_REQUESTED_OBJECT.
            if(responseContainer != null
                    && responseContainer.get(QueueMatcherUtils.DATA_JSON_KEY).
                    equals(QueueMatcherUtils.RESPONSE_LOBBY_DELETED)) {
                break;
            }

            if(responseContainer != null
                    && !responseContainer.get(QueueMatcherUtils.DATA_JSON_KEY).
                    equals(QueueMatcherUtils.RESPONSE_NO_DATA)) {
                dataFound = true;
                break;
            }

            Thread.sleep(4);
            i -= 4;
        }

        } catch (InterruptedException e) {
            Log.e("InterruptedException: " + Thread.currentThread(), e.getStackTrace().toString());
            responseContainer = QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
        } catch (JSONException e) {
            Log.e("JSONException: " + Thread.currentThread(), e.getStackTrace().toString());
            responseContainer = QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
        } finally {
            stillChecking = false;

            // if the data was not found we have to make a cleanup
            if(!dataFound)
                makeDeleteRequest("LobbyChecker");
        }
    }

    public boolean isStillChecking() {
        return stillChecking;
    }

    JSONObject getResponseContainer() {
        return responseContainer;
    }
    JSONObject getUsefullResponseContainer(){
        return usefullResponseContainer;
    }
    synchronized void setResponseContainer(JSONObject responseContainer) {
        this.responseContainer = responseContainer;
    }



    synchronized void setStillChecking(boolean stillChecking) {
        this.stillChecking = stillChecking;
    }

    String getListener() {
        return listener;
    }

    public int getLoopState() {
        return i;
    }

    synchronized void setListener(String listener) {
        this.listener = listener;
    }

    String getCallerSpeaker() {
        return callerSpeaker;
    }

    synchronized void setSpeaker(String speaker) {
       this.callerSpeaker = speaker;
    }

    void makeSpeakerDeleteRequest() {
        if(listener == null || callerSpeaker == null)
            return;

        makeDeleteRequest("deleteSpeaker");
    }

    void makeListenerDeleteRequest() {
        if(listener == null)
            return;

        makeDeleteRequest("deleteListener");
    }

    private void makeDeleteRequest(final String logTag) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                requestUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response: " + logTag, response.toString());
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: " + logTag, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders(){

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(request);
        responseContainer = QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
    }

    private JsonRequest getRequest() {

        return new JsonObjectRequest(Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("LobbyCheckerRunnable: " + Thread.currentThread(), response.toString());
                        responseContainer = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("findSpeakerError", error.toString());
                        responseContainer = QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
                    }
                });
    }
}
