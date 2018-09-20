package fusionkey.lowkey.newsfeed;

import java.util.ArrayList;

public interface NewsFeedCallBack {
    public void retrieveData(ArrayList<Comment> arrayList,String timestamp);
}
