package fusionkey.lowkey.newsfeed;

import java.util.ArrayList;
import java.util.Observable;

import fusionkey.lowkey.newsfeed.Comment;

public class NewsFeedMessage extends Observable {

    public final static String YOUR_QUESTIONS = "yourQuestions";

    public final static String OTHER_QUESTIONS = "otherQuestions";

    public final static String NORMAL = "normal";


    private String content;
    private String date;
    private String user;
    private String answers;
    private String title;
    private Boolean anon;
    private String id;
    private int weekDay;
    private String type;
    public ArrayList<Comment>  commentArrayList;



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public void addCommentToList(Comment m){
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
}
