package fusionkey.lowkey.newsfeed.interfaces;

import java.util.ArrayList;

import fusionkey.lowkey.newsfeed.models.Comment;

public interface NewsFeedCallBack {
    public void retrieveData(ArrayList<Comment> arrayList, String timestamp);
}
