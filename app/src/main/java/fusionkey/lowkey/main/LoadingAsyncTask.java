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

import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.queue.IQueueMatcher;
import fusionkey.lowkey.queue.LobbyCheckerRunnable;
import fusionkey.lowkey.queue.QueueMatcherListenerFinder;
import fusionkey.lowkey.queue.QueueMatcherSpeakerFinder;
import fusionkey.lowkey.queue.QueueMatcherUtils;

public class LoadingAsyncTask extends AsyncTask<Void, Integer, JSONObject> {

    private static final String FIND_LOBBY_TOAST = "The chat is starting!";
    private static final String EXIT_LOBBY_TOAST = "You have exited the loading screen!";
    private static final String LOBBY_DELETED_TOAST = "There are no online listeners or the lobby was deleted";

    private boolean findListener;

    private IQueueMatcher queueMatcher;
    private ProgressBar progressBar;
    private Activity currentActivity;
    private String currentUser;
    private JSONObject jsonResponseContainer;

    LoadingAsyncTask(String currentUser, Activity currentActivity, ProgressBar progressBar, boolean findListener) {
        this.findListener=findListener;
        if (findListener)
            this.queueMatcher = new QueueMatcherListenerFinder(currentUser, currentActivity);
        else
            this.queueMatcher = new QueueMatcherSpeakerFinder(currentUser, currentActivity);
        this.currentUser=currentUser;
        this.currentActivity = currentActivity;
        this.progressBar = progressBar;
        this.currentActivity = currentActivity;
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.progressBar.setMax(LobbyCheckerRunnable.TIME_LOOPING_MILLISECONDS);
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        // Start finding.
        queueMatcher.find();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            queueMatcher.stopFinding();
            e.printStackTrace();
            return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
        }

        // Wait for lobby to get full or to timeout.
        while (queueMatcher.isLoopCheckerAlive() && !isCancelled()) {

            int loopState = queueMatcher.getLoopState();
            if (loopState < 0)
                loopState = 0;
            publishProgress(loopState);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                queueMatcher.stopFinding();
                e.printStackTrace();
                return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
            }
        }

        // Return the response container.
        return queueMatcher.getContainer();
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
            // If there is no data or the request failed don't proceed else do whatever you want to.
            if (jsonObject.equals(QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT) || jsonObject.get(QueueMatcherUtils.DATA_JSON_KEY).equals(QueueMatcherUtils.RESPONSE_NO_DATA)) {
                Log.e("LoadingAsyncTask", "The match was not made successfully");
                Toast.makeText(currentActivity, LOBBY_DELETED_TOAST, Toast.LENGTH_SHORT).show();
                this.cancel(true);
                Main2Activity.SEARCH_STATE=false;

            } else {
                Log.e("LoadingAsyncTask :", jsonObject.toString());
                this.cancel(true);
                Intent intent = new Intent(currentActivity, ChatActivity.class);
                    intent.putExtra("Listener", currentUser);
                    if(!findListener)
                    intent.putExtra("User", jsonObject.getJSONObject("data").getString("speakers"));
                    else
                        intent.putExtra("User", jsonObject.getJSONObject("data").getString("listener"));
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
