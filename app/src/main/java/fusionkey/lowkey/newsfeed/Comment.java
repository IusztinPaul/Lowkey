package fusionkey.lowkey.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Comment implements Parcelable{
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

    public Comment(Parcel read){
        commentIsAnonymous = read.readString();
        commentTStamp = read.readString();
        commentTxt = read.readString();
        commentUserId = read.readString();
    }
    public static final Parcelable.Creator<Comment> CREATOR =
            new Parcelable.Creator<Comment>() {

                @Override
                public Comment createFromParcel(Parcel source) {
                    return new Comment(source);
                }

                @Override
                public Comment[] newArray(int size) {
                    return new Comment[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCommentIsAnonymous());
        dest.writeString(getCommentTStamp());
        dest.writeString(getCommentTxt());
        dest.writeString(getCommentUserId());
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
