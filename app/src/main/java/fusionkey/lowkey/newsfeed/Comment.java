package fusionkey.lowkey.newsfeed;

public class Comment {
    private String commentIsAnonymous;
    private String commentTStamp;
    private String commentTxt;
    private String commentUserId;

    public Comment(String commentIsAnonymous,String commentTStamp,String commentTxt,String commentUserId){
        this.setCommentIsAnonymous(commentIsAnonymous);
        this.setCommentTStamp(commentTStamp);
        this.setCommentTxt(commentTxt);
        this.setCommentUserId(commentUserId);
    }

    public String getCommentIsAnonymous() {
        return commentIsAnonymous;
    }

    public void setCommentIsAnonymous(String commentIsAnonymous) {
        this.commentIsAnonymous = commentIsAnonymous;
    }

    public String getCommentTStamp() {
        return commentTStamp;
    }

    public void setCommentTStamp(String commentTStamp) {
        this.commentTStamp = commentTStamp;
    }

    public String getCommentTxt() {
        return commentTxt;
    }

    public void setCommentTxt(String commentTxt) {
        this.commentTxt = commentTxt;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }
}
