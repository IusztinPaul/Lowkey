package fusionkey.lowkey.queue;

import android.app.Activity;

import fusionkey.lowkey.LowKeyApplication;

public class QueueMatcherFactory {

    private Activity currentActivity;
    private boolean findListener;
    private String currentUserParsedEmail;

    public QueueMatcherFactory(Activity currentActivity, boolean findListener) {
        this.currentActivity = currentActivity;
        this.findListener = findListener;
        this.currentUserParsedEmail = LowKeyApplication.userManager.getParsedUserEmail();
    }

    public IQueueMatcher create() {
        if(findListener)
            return new QueueMatcherListenerFinder(currentUserParsedEmail, currentActivity);
        else
            return new QueueMatcherSpeakerFinder(currentUserParsedEmail, currentActivity);
    }
}
