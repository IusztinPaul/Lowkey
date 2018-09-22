package fusionkey.lowkey.newsfeed;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static fusionkey.lowkey.LowKeyApplication.requestQueueSingleton;

public class NewsfeedRequest {

    private String id;

    private static final String MAIN_API_URL = "https://lqn073orje.execute-api.eu-central-1.amazonaws.com/dev/";
    private static final String MAIN_NEWSFEED_API_URL = MAIN_API_URL;

    private static final String POST_RELATIVE_URL = "post/";
    private static final String COMMENT_RELATIVE_URL = POST_RELATIVE_URL + "comment/";
    private static final String USER_QUESTIONS_RELATIVE_URL = POST_RELATIVE_URL + "user/";

    private  static final String USER_API_QUERY_STRING = "userId";
    private static final String TIME_API_QUERY_STRING = "postTStamp";
    private static final String ANON_API_QUERY_STRING ="isAnonymous";
    private static final String POST_TITLE_API_QUERY_STRING = "postTitle";
    private static final String POST_TEXT_API_QUERY_STRING = "postTxt";
    private static final String PAGE_NUMBER_API_QUERY_STRING = "pageNumber";

    private static final String POST_QUESTION_STRING ="postQuestion";
    public static final String GET_QUESTION_STRING ="getQuestion";
    private static final String DELETE_QUESTION_STRING ="deleteQuestion";
    private static final String POST_COMMENT_STRING ="postComment";

    public static final String RESPONSE_NO_DATA = "";
    public static final String RESPONSE_ERROR = "error";
    public static final String DATA_JSON_KEY = "data";
    public static final String NO_DATA = "the response has no data";

    public NewsfeedRequest(String id){
        this.setId(id);
    }



    public void postQuestion(Long time,Boolean anon,String title,String text){

        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, getId());
        queryParameters.put(TIME_API_QUERY_STRING,String.valueOf(time));

        Map<String, String> params = new HashMap<String, String>();
        params.put(ANON_API_QUERY_STRING,Boolean.toString(anon));
        params.put(POST_TITLE_API_QUERY_STRING, title);
        params.put(POST_TEXT_API_QUERY_STRING, text);

        String URL = getAbsoluteUrlWithQueryString(queryParameters, POST_RELATIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, URL,new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(POST_QUESTION_STRING, response.toString());
                        try {
                            if (!response.getString(DATA_JSON_KEY).equals("Post saved"))
                                Log.e(POST_QUESTION_STRING, NO_DATA);
                        } catch (JSONException e) {
                            Log.e(POST_QUESTION_STRING, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(RESPONSE_ERROR, error.toString());
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }



    public void getNewsfeed(int number,final NewsfeedVolleyCallBack listener){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(PAGE_NUMBER_API_QUERY_STRING, String.valueOf(number));

        String URL = getAbsoluteUrlWithQueryString(queryParameters, POST_RELATIVE_URL);

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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }



    public void deleteQuestion(String time){

        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(TIME_API_QUERY_STRING,time);

        String URL = getAbsoluteUrlWithQueryString(queryParameters, POST_RELATIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(DELETE, URL,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(DELETE_QUESTION_STRING, response.toString());
                        try {
                            if (!response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA))
                                Log.e(DELETE_QUESTION_STRING, NO_DATA);
                        } catch (JSONException e) {
                            Log.e(DELETE_QUESTION_STRING, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(RESPONSE_ERROR, error.toString());
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }



    public void postComment(String time,Boolean anon,String text){

        HashMap<String,String> queryParameters = new HashMap<>();

        queryParameters.put(TIME_API_QUERY_STRING,time.toString());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("commentTxt", text);
            jsonBody.put("commentUserId", getId());
            jsonBody.put("commentTStamp", String.valueOf(timestamp.getTime()));
            jsonBody.put("commentIsAnonymous", Boolean.toString(anon));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String URL = getAbsoluteUrlWithQueryString(queryParameters, COMMENT_RELATIVE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, URL,jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(POST_COMMENT_STRING, response.toString());
                        try {
                            if (!response.get(DATA_JSON_KEY).equals(RESPONSE_NO_DATA))
                                Log.e(POST_COMMENT_STRING, NO_DATA);
                        } catch (JSONException e) {
                            Log.e(POST_COMMENT_STRING, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(RESPONSE_ERROR, error.toString());
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }



    public void getYourQuestions(final NewsfeedVolleyCallBack listener){
        HashMap<String,String> queryParameters = new HashMap<>();
        queryParameters.put(USER_API_QUERY_STRING, getId());

        String URL = getAbsoluteUrlWithQueryString(queryParameters, USER_QUESTIONS_RELATIVE_URL);

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
                HashMap<String, String> headers = new HashMap<>();
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
        return MAIN_NEWSFEED_API_URL + relativeUrl;
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
