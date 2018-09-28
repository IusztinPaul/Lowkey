package fusionkey.lowkey.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import fusionkey.lowkey.newsfeed.models.Comment;


public class MyParcelable implements Parcelable {

    private List<Comment> arrList = new ArrayList<Comment>();
    private int myInt = 0;
    private String str = null;


    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public List<Comment> getArrList() {
        return arrList;
    }

    public void setArrList(List<Comment> arrList) {
        this.arrList = arrList;
    }

    public int getMyInt() {
        return myInt;
    }

    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }

    MyParcelable() {
        // initialization
        arrList = new ArrayList<Comment>();
    }

    public MyParcelable(Parcel in) {
        myInt = in.readInt();
        str = in.readString();
        in.readTypedList(arrList, Comment.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outParcel, int flags) {
        outParcel.writeInt(myInt);
        outParcel.writeString(str);
        outParcel.writeTypedList(arrList);
    }

    public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {

        @Override
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        @Override
        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };
}