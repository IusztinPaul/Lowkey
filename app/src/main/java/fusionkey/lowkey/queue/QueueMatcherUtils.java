package fusionkey.lowkey.queue;

import android.app.Activity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Iusztin Paul
 * @version 1.0
 * @since 31.07.2018
 *
 * <h1>Class that contains the API constants
 * and has some util methods for building urls</h1>
 */
abstract public class QueueMatcherUtils {

    static final String MAIN_API_URL = "https://gra4ddmrz6.execute-api.eu-central-1.amazonaws.com/dev/";
    static final String MAIN_QUEUE_API_URL = MAIN_API_URL + "queue/";

    static final String LISTENER_RELATIVE_URL = "listener/";
    static final String SEAPKER_RELATIVE_URL = "speaker/";

    public static final String DATA_JSON_KEY = "data";
    public static final String ERROR_JSON_LEY = "errorMessage";
    public static final String STATUS_CODE_JSON_KEY = "statusCode";
    public static final String DELETE_INFO_JSON_KEY = "deleteInfoMessage";
    public static final String RESPONSE_NO_DATA = "";
    public static final String RESPONSE_LOBBY_DELETED = "$lobby_deleted$";

    public static final String DATA_SPEAKERS_KEY = "speakers";
    public static final String DATA_LISTENER_KEY = "listener";

    static final String USER_API_QUERY_STRING = "user";
    static final String LISTENER_API_QUERY_STRING = "listener";

    public static final JSONObject JSON_FAILED_REQUESTED_OBJECT = new JSONObject();

    String currentUser;
    Activity currentActivity;

    LobbyCheckerRunnable findRunnable;

    QueueMatcherUtils(String currentUser, Activity currentActivity) {
        this.currentUser = currentUser;
        this.currentActivity = currentActivity;
    }

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
