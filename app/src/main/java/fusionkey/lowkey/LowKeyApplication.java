package fusionkey.lowkey;

import android.app.Application;

import fusionkey.lowkey.login.utils.UserManager;
import fusionkey.lowkey.queue.RequestQueueSingleton;


public class LowKeyApplication extends Application {

    public static RequestQueueSingleton requestQueueSingleton;
    public static UserManager loginManager;

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueueSingleton = RequestQueueSingleton.getInstance(this);
        loginManager = UserManager.getInstance(this);
    }

}
