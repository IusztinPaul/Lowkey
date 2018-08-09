package fusionkey.lowkey.queue;

import org.json.JSONObject;

public interface IQueueMatcher {
    void find();
    JSONObject getContainer();
    void stopFinding();
    boolean isLoopCheckerAlive();
    int getLoopState();
}
