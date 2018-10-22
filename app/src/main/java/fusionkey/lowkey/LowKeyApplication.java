package fusionkey.lowkey;

import android.app.Application;
import android.graphics.Bitmap;

import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.queue.RequestQueueSingleton;


public class LowKeyApplication extends Application {
    public static Bitmap profilePhoto;
    public static String endpointArn;

    public static RequestQueueSingleton requestQueueSingleton;
    public static UserManager userManager;

    public static LowKeyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        requestQueueSingleton = RequestQueueSingleton.getInstance(this);
        userManager = UserManager.getInstance(this);
    }

}
