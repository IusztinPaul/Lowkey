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

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.queue.IQueueMatcher;
import fusionkey.lowkey.queue.LobbyCheckerRunnable;
import fusionkey.lowkey.queue.QueueMatcherFactory;
import fusionkey.lowkey.queue.QueueMatcherUtils;

public class LoadingAsyncTask extends AsyncTask<Void, Integer, JSONObject> {

    private static final String FIND_LOBBY_TOAST = "The chat is starting!";
    private static final String EXIT_LOBBY_TOAST = "You have exited the loading screen!";
    private static final String LOBBY_DELETED_TOAST = "There are no online users at the moment.";

    private boolean findListener;

    private IQueueMatcher queueMatcher;
    private WeakReference<ProgressBar> progressBar;
    private WeakReference<Activity> currentActivity;
    private WeakReference<CardView> searchCard;
    private UserDB currentUser;
    private JSONObject jsonResponseContainer;

    LoadingAsyncTask(Activity currentActivity,
                     ProgressBar progressBar,
                     boolean findListener,
                     CardView searchCard) {
        this.findListener = findListener;
        this.currentUser = LowKeyApplication.userManager.getUserDetails();
        this.searchCard = new WeakReference<>(searchCard);
        this.currentActivity = new WeakReference<>(currentActivity);

        this.progressBar = new WeakReference<>(progressBar);
        this.progressBar.get().setVisibility(View.GONE);

        this.queueMatcher = new QueueMatcherFactory(currentActivity, findListener).create();
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.get().setVisibility(View.VISIBLE);
        this.progressBar.get().setMax(LobbyCheckerRunnable.TIME_LOOPING_MILLISECONDS);
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        if (NetworkManager.isNetworkAvailable()) {
            // Start finding.
            queueMatcher.find();

            // Wait for the L0/S0 API calls to finish and have a response.
            while (!queueMatcher.hasStep0Response())
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    queueMatcher.stopFinding();
                    e.printStackTrace();
                    return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
                }

            // Wait for lobby to get full or to timeout.
            while (queueMatcher.isLoopCheckerAlive() &&
                    !isCancelled() &&
                    NetworkManager.isNetworkAvailable()) {

                updateProgressBarProgress();

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
        queueMatcher.stopFinding();
        stopAndResetProgressBar();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        this.jsonResponseContainer = jsonObject;

        stopAndResetProgressBar();
        processSearchCardState();

        if (!isContainerValid()) {
            Log.e("LoadingAsyncTask", "The match was not made successfully");
            Toast.makeText(currentActivity.get(), LOBBY_DELETED_TOAST, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("LoadingAsyncTask :", jsonObject.toString());
            Toast.makeText(this.currentActivity.get(), FIND_LOBBY_TOAST, Toast.LENGTH_SHORT).show();

            Intent intent = createIntent();
            currentActivity.get().startActivity(intent);
        }
    }

    private void updateProgressBarProgress() {
        int loopState = queueMatcher.getLoopState();

        if (loopState < 0)
            loopState = 0; // ProgressBar progress cannot be negative.

        publishProgress(loopState);
    }


    private boolean isContainerValid() {
        try {
            return !jsonResponseContainer.equals(QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT) &&
                    !jsonResponseContainer.get(QueueMatcherUtils.DATA_JSON_KEY).equals(QueueMatcherUtils.RESPONSE_NO_DATA);
        } catch (JSONException e) {
            Log.e("isContainerValid", e.getMessage());
            return false;
        }
    }

    private Intent createIntent() {
        try {
            Intent intent = new Intent(currentActivity.get(), ChatActivity.class);
            intent.putExtra(ChatActivity.LISTENER_INTENT, currentUser.getUserEmail());

            if (!findListener) {
                intent.putExtra(ChatActivity.USER_INTENT,
                        jsonResponseContainer.getJSONObject(QueueMatcherUtils.DATA_JSON_KEY).
                                getString(QueueMatcherUtils.DATA_SPEAKERS_KEY));
                intent.putExtra(ChatActivity.ROLE_INTENT, "helper");
            } else {
                intent.putExtra(ChatActivity.USER_INTENT,
                        jsonResponseContainer.getJSONObject(QueueMatcherUtils.DATA_JSON_KEY).
                                getString(QueueMatcherUtils.DATA_LISTENER_KEY));
                intent.putExtra(ChatActivity.ROLE_INTENT, "Nothelper");
            }

            return intent;
        } catch (JSONException e) {
            Log.e("createIntent", e.getMessage());
            return null;
        }
    }

    private void processSearchCardState() {
        saveState("step", 0);
        searchCard.get().setVisibility(View.INVISIBLE);
    }

    private void saveState(String key, int step) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentActivity.get().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, step);
        editor.apply();
    }

    private void stopAndResetProgressBar() {
        this.progressBar.get().setVisibility(View.GONE);
        this.progressBar.get().setProgress(progressBar.get().getMax());
    }

    private int loadState() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentActivity.get().getApplicationContext());
        return (sharedPreferences.getInt("step", 0));
    }

    public JSONObject getJsonResponseContainer() {
        return jsonResponseContainer;
    }
}
