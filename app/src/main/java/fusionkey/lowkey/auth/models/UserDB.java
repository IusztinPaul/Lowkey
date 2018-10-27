package fusionkey.lowkey.auth.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "lowkey-mobilehub-1217601830-Users")
public class UserDB {
    private String _userEmail; // User email. Not parsed.
    private String _birthDate;
    private String _gender;
    private String _fullName;
    private String _username;
    private String _phone;
    private Long _score;
    private List<Long> _timeStamps;

    public UserDB(String _userEmail) {
        this._userEmail = _userEmail;
    }

    public UserDB(String _userEmail, String _username) {
        this._userEmail = _userEmail;
        this._username = _username;
    }

    public UserDB(String _userEmail, String _birthDate, String _gender, String _fullName,
                  String _username, String _phone, Long _score, List<Long> _timeStamps) {
        this._userEmail = _userEmail;
        this._birthDate = _birthDate;
        this._gender = _gender;
        this._fullName = _fullName;
        this._username = _username;
        this._phone = _phone;
        this._score = _score;
        this._timeStamps = _timeStamps;
    }

    public UserDB(String _userEmail, String _birthDate, String _gender,
                  String _fullName, String _username, String _phone) {
        this._userEmail = _userEmail;
        this._birthDate = _birthDate;
        this._gender = _gender;
        this._fullName = _fullName;
        this._username = _username;
        this._phone = _phone;
    }

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserEmail() {
        return _userEmail;
    }

    public void setUserId(final String _userId) {
        this._userEmail = _userId;
    }

    @DynamoDBAttribute(attributeName = "birthDate")
    public String getBirthDate() {
        return _birthDate;
    }

    public void setBirthDate(String _birthDate) {
        this._birthDate = _birthDate;
    }

    @DynamoDBAttribute(attributeName = "gender")
    public String getGender() {
        return _gender;
    }

    public void setGender(String _gender) {
        this._gender = _gender;
    }

    @DynamoDBAttribute(attributeName = "fullName")
    public String getFullName() {
        return _fullName;
    }

    public void setFullName(String _name) {
        this._fullName = _name;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return _username;
    }

    public void setUsername(String _username) {
        this._username = _username;
    }

    @DynamoDBAttribute(attributeName = "phone")
    public String getPhone() {
        return _phone;
    }

    public void setPhone(String _phone) {
        this._phone = _phone;
    }

    @DynamoDBAttribute(attributeName = "score")
    public Long getScore() {
        return _score;
    }

    public void setScore(final Long _score) {
        this._score = _score;
    }

    @DynamoDBAttribute(attributeName = "timeStamps")
    public List<Long> getTimeStamps() {
        return _timeStamps;
    }

    public void setTimeStamps(List<Long> _timeStamps) {
        this._timeStamps = _timeStamps;
    }

    @Override
    public String toString() {
        return "UserDB{" +
                "userEmail='" + _userEmail + '\'' +
                ", fullName='" + _fullName + '\'' +
                ", username='" + _username + '\'' +
                '}';
    }
}
