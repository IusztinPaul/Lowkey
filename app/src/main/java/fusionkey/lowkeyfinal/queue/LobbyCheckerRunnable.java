package ro.fusionkey.lowkey.queue;

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

import static ro.fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;


public class LobbyCheckerRunnable implements Runnable {

    private final int TIME_LOOPING_MILLISECONDS = 30 * 1000;

    private String requestUrl;
    private String listener;
    private String callerSpeaker;

    private boolean stillChecking;
    private JSONObject responseContainer;

    LobbyCheckerRunnable(String requestUrl, String listener, String callerSpeaker) {
        this.requestUrl = requestUrl;
        this.listener = listener;
        this.callerSpeaker = callerSpeaker;
        this.stillChecking = false;
    }

    @Override
    public void run() {
        int i = TIME_LOOPING_MILLISECONDS;
        boolean dataFound = false;
        stillChecking = true;

        try {
        while (stillChecking && i > 0) {

            responseContainer = null;
            requestQueueSingleton.addToRequestQueue(getRequest());

            while (responseContainer == null) {
                Thread.sleep(2);
                i -= 2;

                if(i <= 0)
                    break;
            }

            if(responseContainer != null && !responseContainer.get("data").equals("")) {
                dataFound = true;
                break;
            }

            Thread.sleep(4);
            i -= 4;
        }

        } catch (InterruptedException e) {
            Log.e("InterruptedException: " + Thread.currentThread(), e.getStackTrace().toString());
            responseContainer = new JSONObject();
        } catch (JSONException e) {
            Log.e("JSONException: " + Thread.currentThread(), e.getStackTrace().toString());
            responseContainer = new JSONObject();
        } finally {
            stillChecking = false;

            // if the data was not found we have to make a cleanup
            if(!dataFound)
                makeDeleteRequest("Queue not match");
        }
    }

    boolean isStillChecking() {
        return stillChecking;
    }

    JSONObject getResponseContainer() {
        return responseContainer;
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
                        responseContainer = new JSONObject();
                    }
                });
    }
}
