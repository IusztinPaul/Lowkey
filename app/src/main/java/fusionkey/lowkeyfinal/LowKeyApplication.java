package fusionkey.lowkeyfinal;

import android.app.Application;

import fusionkey.lowkeyfinal.queue.RequestQueueSingleton;


public class LowKeyApplication extends Application {

    public static RequestQueueSingleton requestQueueSingleton;

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueueSingleton = RequestQueueSingleton.getInstance(this);
    }

}
