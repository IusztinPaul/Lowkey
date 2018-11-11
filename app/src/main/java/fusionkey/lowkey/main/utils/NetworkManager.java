package fusionkey.lowkey.main.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import fusionkey.lowkey.LowKeyApplication;

public class NetworkManager {

    public static boolean isNetworkAvailable() {
        // Just short circuit this logic for emulator.
        if(EmulatorUtils.isEmulator()) {
            Log.e("Emulator short circuit",
                    "For emulator environment the network check is skipped");
            return true;
        }

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
