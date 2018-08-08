package fusionkey.lowkey.main;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fusionkey.lowkey.ChatActivity;
import fusionkey.lowkey.queue.LobbyCheckerRunnable;
import fusionkey.lowkey.queue.QueueMatcher;

public class LoadingAsyncTask extends AsyncTask<Void, Integer, JSONObject> {

    public static final String FIND_LOBBY_TOAST = "The chat is starting!";
    public static final String EXIT_LOBBY_TOAST = "You have exited the loading screen!";

    private QueueMatcher queueMatcher;
    private ProgressBar progressBar;
    private boolean findListener;
    private Activity currentActivity;
    private JSONObject jsonResponseContainer;

    LoadingAsyncTask(String currentUser, Activity currentActivity, ProgressBar progressBar, boolean findListener) {
        this.queueMatcher = new QueueMatcher(currentUser, currentActivity);
        this.currentActivity = currentActivity;
        this.progressBar = progressBar;
        this.currentActivity = currentActivity;
        this.progressBar.setVisibility(View.GONE);
        this.findListener = findListener;
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.progressBar.setMax(LobbyCheckerRunnable.TIME_LOOPING_MILLISECONDS);
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        if (findListener)
            queueMatcher.findListener();
        else
            queueMatcher.findSpeakers();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

            if (findListener)
                queueMatcher.stopFindingListener();
            else
                queueMatcher.stopFindingSpeaker();

            e.printStackTrace();
            return QueueMatcher.JSON_FAILED_REQUESTED_OBJECT;
        }

        if (findListener)
            while (queueMatcher.isLoopCheckerAliveListener() && !isCancelled()) {

                int loopState = queueMatcher.getLoopStateListener();
                if(loopState < 0)
                    loopState = 0;
                publishProgress(loopState);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    queueMatcher.stopFindingListener();
                    e.printStackTrace();
                    return QueueMatcher.JSON_FAILED_REQUESTED_OBJECT;
                }
            }
        else
            while (queueMatcher.isLoopCheckerAliveSpeaker() && !isCancelled()) {
                publishProgress(queueMatcher.getLoopStateSpeaker());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    queueMatcher.stopFindingSpeaker();
                    e.printStackTrace();
                    return QueueMatcher.JSON_FAILED_REQUESTED_OBJECT;
                }
            }

        if (findListener)
            return queueMatcher.getListener();
        else
            return queueMatcher.getSpeakers();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {
        Toast.makeText(this.currentActivity, EXIT_LOBBY_TOAST, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        this.progressBar.setVisibility(View.GONE);
        this.jsonResponseContainer = jsonObject;

        try {
            if (jsonObject.equals(QueueMatcher.JSON_FAILED_REQUESTED_OBJECT) || jsonObject.get(QueueMatcher.DATA_JSON_KEY).equals(QueueMatcher.RESPONSE_NO_DATA)) {
                Log.e("LoadingAsyncTask : ", "The match was not made successfully");
            } else {
                Log.e("LoadingAsyncTask :", jsonObject.toString());
                Intent intent = new Intent(currentActivity, ChatActivity.class);
                currentActivity.startActivity(intent);
                Toast.makeText(this.currentActivity, FIND_LOBBY_TOAST, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("LoadingAsyncTask", e.getMessage());
        }


    }


    public JSONObject getJsonResponseContainer() {
        return jsonResponseContainer;
    }
}
