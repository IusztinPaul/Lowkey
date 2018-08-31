package fusionkey.lowkey;

import android.app.Application;

import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.queue.RequestQueueSingleton;


public class LowKeyApplication extends Application {

    public static RequestQueueSingleton requestQueueSingleton;
    public static UserManager loginManager;

    public static LowKeyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        requestQueueSingleton = RequestQueueSingleton.getInstance(this);
        loginManager = UserManager.getInstance(this);
    }

}
