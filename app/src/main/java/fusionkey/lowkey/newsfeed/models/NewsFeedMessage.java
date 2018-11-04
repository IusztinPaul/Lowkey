package fusionkey.lowkey.newsfeed.models;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;

public class NewsFeedMessage extends Observable {

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
    private ArrayList<Comment> commentArrayList;
    private Bitmap userPhoto;
    private String SNStopic;
    private File file;


    public NewsFeedMessage() {
    }

    public NewsFeedMessage(Long timeStamp) {
        this.timeStamp = timeStamp;
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

    public String getSNStopic() {
        return SNStopic;
    }

    public void setSNStopic(String SNStopic) {
        this.SNStopic = SNStopic;
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