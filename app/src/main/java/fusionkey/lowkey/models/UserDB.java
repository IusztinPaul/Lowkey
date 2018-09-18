package fusionkey.lowkey.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "lowkey-mobilehub-1217601830-Users")
public class UserDB {
    private String _userId;
    private Long _score;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }

    @DynamoDBAttribute(attributeName = "score")
    public Long getScore() {
        return _score;
    }

    public void setScore(final Long _score) {
        this._score = _score;
    }

    @Override
    public String toString() {
        return "UserDB{" +
                "userId='" + _userId + '\'' +
                ", score=" + _score +
                '}';
    }
}
