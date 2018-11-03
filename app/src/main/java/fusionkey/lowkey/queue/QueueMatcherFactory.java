package fusionkey.lowkey.queue;

import android.app.Activity;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.models.UserDB;

public class QueueMatcherFactory {

    private Activity currentActivity;
    private boolean findListener;
    private UserDB currentUser;

    public QueueMatcherFactory(Activity currentActivity, boolean findListener) {
        this.currentActivity = currentActivity;
        this.findListener = findListener;
        this.currentUser = LowKeyApplication.userManager.getUserDetails();
    }

    public IQueueMatcher create() {
        if(findListener)
            return new QueueMatcherListenerFinder(currentUser, currentActivity);
        else
            return new QueueMatcherSpeakerFinder(currentUser, currentActivity);
    }
}
