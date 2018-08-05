package fusionkey.lowkey.queue;

import org.json.JSONObject;

public interface IQueueMatcher {
    void findSpeakers();
    void findListener();
    JSONObject getSpeakers();
    JSONObject getListener();
    void stopFindingSpeaker();
    void stopFindingListener();
}
