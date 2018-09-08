package fusionkey.lowkey.main.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import fusionkey.lowkey.LowKeyApplication;

public class NetworkManager {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) LowKeyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null &&
                    activeNetworkInfo.isConnected() &&
                    activeNetworkInfo.isAvailable() &&
                    isConnected();
        }
        return false;
    }

    private static boolean isConnected()
    {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}
