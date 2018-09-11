package fusionkey.lowkey.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.main.utils.NetworkManager;
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
    private WeakReference<ProgressBar> progressBar;
    private WeakReference<Activity> currentActivity;
    private WeakReference<CardView> searchCard;
    private String currentUser;
    private JSONObject jsonResponseContainer;

    LoadingAsyncTask(String currentUser, Activity currentActivity, ProgressBar progressBar, boolean findListener,CardView searchCard) {
        this.findListener=findListener;
        if (findListener)
            this.queueMatcher = new QueueMatcherListenerFinder(currentUser, currentActivity);
        else
            this.queueMatcher = new QueueMatcherSpeakerFinder(currentUser, currentActivity);
        this.currentUser=currentUser;
        this.searchCard = new WeakReference<>(searchCard);
        this.currentActivity = new WeakReference<>(currentActivity);
        this.progressBar = new WeakReference<>(progressBar);
        this.progressBar.get().setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.get().setVisibility(View.VISIBLE);
        this.progressBar.get().setMax(LobbyCheckerRunnable.TIME_LOOPING_MILLISECONDS);
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        if(NetworkManager.isNetworkAvailable()) {
            // Start finding.
            queueMatcher.find();

            // This time is needed so the runnables from the queueMatcher can start. Otherwise
            // the container will be null and queueMatcher.isLoopCheckerAlive() == FALSE
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                queueMatcher.stopFinding();
                e.printStackTrace();
                return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
            }

            // Wait for lobby to get full or to timeout.
            while (queueMatcher.isLoopCheckerAlive() &&
                    !isCancelled() &&
                    NetworkManager.isNetworkAvailable()) {

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

        return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.get().setProgress(values[0]);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {
        Toast.makeText(this.currentActivity.get(), EXIT_LOBBY_TOAST, Toast.LENGTH_SHORT).show();
        this.progressBar.get().setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        this.progressBar.get().setVisibility(View.GONE);
        this.jsonResponseContainer = jsonObject;

        try {
            // If there is no data or the request failed don't proceed else do whatever you want to.
            if (jsonObject.equals(QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT) || jsonObject.get(QueueMatcherUtils.DATA_JSON_KEY).equals(QueueMatcherUtils.RESPONSE_NO_DATA)) {
                Log.e("LoadingAsyncTask", "The match was not made successfully");
                Toast.makeText(currentActivity.get(), LOBBY_DELETED_TOAST, Toast.LENGTH_SHORT).show();
                saveState("step",0);
                searchCard.get().setVisibility(View.INVISIBLE);
            } else {
                Log.e("LoadingAsyncTask :", jsonObject.toString());
                Intent intent = new Intent(currentActivity.get(), ChatActivity.class);
                intent.putExtra("Listener", currentUser);
                    if(!findListener)
                        intent.putExtra("User",
                                jsonObject.getJSONObject(QueueMatcherUtils.DATA_JSON_KEY).
                                        getString(QueueMatcherUtils.DATA_SPEAKERS_KEY));
                    else
                        intent.putExtra("User",
                                jsonObject.getJSONObject(QueueMatcherUtils.DATA_JSON_KEY).
                                        getString(QueueMatcherUtils.DATA_LISTENER_KEY));

                saveState("step",0);
               searchCard.get().setVisibility(View.INVISIBLE);
                    currentActivity.get().startActivity(intent);
                Toast.makeText(this.currentActivity.get(), FIND_LOBBY_TOAST, Toast.LENGTH_SHORT).show();



            }
        } catch (JSONException e) {
            Log.e("LoadingAsyncTask", e.getMessage());
        }
    }
    private void saveState(String key,int step){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentActivity.get().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, step);
        editor.apply();
    }
    private int loadState(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentActivity.get().getApplicationContext());
        return (sharedPreferences.getInt("step", 0));
    }

    public JSONObject getJsonResponseContainer() {
        return jsonResponseContainer;
    }
}
