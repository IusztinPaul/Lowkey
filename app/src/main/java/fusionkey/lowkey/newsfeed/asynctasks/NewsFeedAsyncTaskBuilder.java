package fusionkey.lowkey.newsfeed.asynctasks;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import fusionkey.lowkey.listAdapters.NewsFeedAdapter;
import fusionkey.lowkey.newsfeed.interfaces.IGenericConsumer;
import fusionkey.lowkey.newsfeed.models.NewsFeedMessage;
import fusionkey.lowkey.newsfeed.util.NewsFeedRequest;

public class NewsFeedAsyncTaskBuilder {
    private final long START_REFERENCE_TIMESTAMP_VALUE = -1L;

    private NewsFeedRequest newsFeedRequest;
    private ArrayList<NewsFeedMessage> messages;
    private RecyclerView recyclerView;
    private NewsFeedAdapter newsFeedAdapter;

    private Long referenceTimestamp = START_REFERENCE_TIMESTAMP_VALUE;
    private IGenericConsumer<Long> setter;
    private boolean isNew = false;
    private boolean isStart = false;

    public NewsFeedAsyncTaskBuilder(NewsFeedRequest newsFeedRequest,
                                    ArrayList<NewsFeedMessage> messages,
                                    RecyclerView recyclerView,
                                    NewsFeedAdapter newsFeedAdapter) {

        this.newsFeedRequest = newsFeedRequest;
        this.messages = messages;
        this.recyclerView = recyclerView;
        this.newsFeedAdapter = newsFeedAdapter;
    }

    public NewsFeedAsyncTaskBuilder addReferenceTimeSTamp(Long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        return this;
    }

    public NewsFeedAsyncTaskBuilder addSetter(IGenericConsumer<Long> setter) {
        this.setter = setter;
        return this;
    }

    /**
     * Used when new posts are added.
     */
    public NewsFeedAsyncTaskBuilder addArePostNew() {
        this.isNew = true;
        return this;
    }

    /**
     * If it's true it will query from the current item, otherwise it will get the items from
     * the next one relative to the referenceTimestamp/
     * If referenceTimestamp is START_REFERENCE_TIMESTAMP_VALUE this value is ignored.
     */
    public NewsFeedAsyncTaskBuilder addIsStart() {
        this.isStart = true;
        return this;
    }

    public NewsFeedAsyncTask build() {
        return new NewsFeedAsyncTask(
                messages,
                recyclerView,
                newsFeedAdapter,
                newsFeedRequest,
                referenceTimestamp,
                setter,
                isNew,
                isStart
        );
    }
}
