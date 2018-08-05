package fusionkey.lowkeyfinal.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import fusionkey.lowkeyfinal.queue.LobbyCheckerRunnable;
import fusionkey.lowkeyfinal.queue.QueueMatcher;

import static com.android.volley.Request.Method.HEAD;

public class LoadingAsyncTask extends AsyncTask<Void, Integer, JSONObject> {

    private QueueMatcher queueMatcher;
    private ProgressBar progressBar;
    private boolean findListener;

    private JSONObject jsonResponseContainer;

    LoadingAsyncTask(String currentUser, Activity currentActivity, ProgressBar progressBar, boolean findListener) {
        this.queueMatcher = new QueueMatcher(currentUser, currentActivity);
        this.progressBar = progressBar;
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
            Thread.sleep(500);
        } catch (InterruptedException e) {

            if (findListener)
                queueMatcher.stopFindingListener();
            else
                queueMatcher.stopFindingSpeaker();

            e.printStackTrace();
            return null;
        }

        if (findListener)
            while (queueMatcher.isLoopCheckerAliveListener()) {
                publishProgress(queueMatcher.getLoopStateListener());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    queueMatcher.stopFindingListener();
                    e.printStackTrace();
                    return null;
                }
            }
        else
            while (queueMatcher.isLoopCheckerAliveSpeaker()) {
                publishProgress(queueMatcher.getLoopStateSpeaker());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    queueMatcher.stopFindingSpeaker();
                    e.printStackTrace();
                    return null;
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
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        this.progressBar.setVisibility(View.GONE);
        this.jsonResponseContainer = jsonObject;

    }
}
