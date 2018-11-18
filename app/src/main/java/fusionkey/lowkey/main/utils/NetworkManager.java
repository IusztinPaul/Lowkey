package fusionkey.lowkey.main.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import fusionkey.lowkey.LowKeyApplication;

public class NetworkManager {

    public static boolean isNetworkAvailable() {
        // Just short circuit this logic for emulator.
        if (EmulatorUtils.isEmulator()) {
            Log.e("Emulator short circuit",
                    "For emulator environment the network check is skipped");
            return true;
        }

        return pingCheck() || requestCheck();
    }

    private static boolean pingCheck() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() < 5);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean requestCheck() {

        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ConnectivityManager cm
                        = (ConnectivityManager) LowKeyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork;
                try {
                    activeNetwork = cm.getActiveNetworkInfo();
                } catch (NullPointerException e) {
                    Log.e("requestCheck", e.getMessage());
                    return false;
                }

                if (activeNetwork != null &&
                    activeNetwork.isConnected() &&
                    activeNetwork.isAvailable()) {
                        return makeRequest();
                }

                return false;
            }
        };

        return runInBackground(callable);
    }

    private static boolean makeRequest() {
        try {
            URL url = new URL("http://www.google.com/");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000); // mTimeout is in seconds
            urlc.connect();

            return (urlc.getResponseCode() == 200);

        } catch (IOException e) {
            Log.e("makeRequest", "Error checking internet connection", e);
            return false;
        }
    }

    private static boolean runInBackground(Callable<Boolean> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> futureTask = new FutureTask<>(callable);
        try {
            executor.submit(futureTask);
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        } finally {
            executor.shutdown();
        }
    }
}
