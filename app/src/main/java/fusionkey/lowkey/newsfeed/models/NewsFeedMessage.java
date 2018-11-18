package fusionkey.lowkey.newsfeed.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.main.utils.JsonExtractor;

public class NewsFeedMessage extends Observable {
    public final static String KEY_SNS_TOPIC = "snsTopic";

    public final static String YOUR_QUESTIONS = "yourQuestions";

    public final static String OTHER_QUESTIONS = "otherQuestions";

    public final static String NORMAL = "normal";

    private String content;
    private Long timeStamp;
    private String user;
    private String answers;
    private String title;
    private Boolean anon;
    private String id;
    private int weekDay;
    private String type;
    private ArrayList<Comment> commentArrayList = new ArrayList<>();
    private Bitmap userPhoto;
    private String SNSTopic;
    private File file;


    public NewsFeedMessage() { }

    public NewsFeedMessage(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public NewsFeedMessage(JSONObject jsonObject) {
        populate(jsonObject);
    }

    public void populate(JSONObject jsonObject) {
        JsonExtractor extractor = new JsonExtractor(jsonObject);
        this.weekDay = extractor.extractInteger("weekDay");
        this.id = extractor.extractString("userId");
        this.content = extractor.extractString("postTxt");
        this.timeStamp = extractor.extractLong("postTStamp");
        this.title = extractor.extractString("postTitle");
        this.SNSTopic = extractor.extractString(KEY_SNS_TOPIC);
        this.anon = extractor.extractBoolean("isAnonymous");

        UserAttributeManager userAttributeManager = new UserAttributeManager(id);
        this.user = userAttributeManager.getUsername();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getTitle() {
        return title;
    }

    public void addCommentToList(Comment m) {
        commentArrayList.add(m);
    }

    public void addCommentsToList(List<Comment> comments) {
        commentArrayList.addAll(comments);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAnon() {
        return anon;
    }

    public void setAnon(Boolean anon) {
        this.anon = anon;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getSNSTopic() {
        return SNSTopic;
    }

    public void setSNSTopic(String SNSTopic) {
        this.SNSTopic = SNSTopic;
    }

    public void setSNSTopicFromResponse(JSONObject response) {
        try {
            this.SNSTopic = response.getString(KEY_SNS_TOPIC);
        } catch (JSONException e) {
            Log.e("setSNSTopicFromResponse", e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsFeedMessage)) return false;

        NewsFeedMessage that = (NewsFeedMessage) o;
        return timeStamp.equals(that.timeStamp);
    }

    @Override
    public int hashCode() {
        long timestamp = timeStamp;
        return 11 * (int) timestamp + 5;
    }
}