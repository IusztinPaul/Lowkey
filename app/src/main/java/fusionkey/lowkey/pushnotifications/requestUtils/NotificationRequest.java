package fusionkey.lowkey.pushnotifications.requestUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import fusionkey.lowkey.chat.interfaces.VolleyResponseListener;

import static com.android.volley.Request.Method.GET;
import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

public class NotificationRequest {

    private static final String MAIN_API_URL = "https://lqn073orje.execute-api.eu-central-1.amazonaws.com/dev/";
    private static final String MAIN_NOTIF_API_URL = MAIN_API_URL + "post/";
    private static final String GETBYTIMESTAMP_RELATIVE_RUL = "timestamp/";
    private static final String QUERY_TIMESTAMP_STRING ="postTStamp";
    public static final String RESPONSE_ERROR = "ErrorNotif";

    private String timestamp;

    public NotificationRequest(String timestamp){ this.timestamp=timestamp; }

    public void getQuestion(final INotificationResponseListener listener){

        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(QUERY_TIMESTAMP_STRING, this.timestamp);
        String URL = getAbsoluteUrlWithQueryString(queryParameters, GETBYTIMESTAMP_RELATIVE_RUL);
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
        return MAIN_NOTIF_API_URL + relativeUrl;
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
