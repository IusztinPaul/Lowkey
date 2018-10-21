package fusionkey.lowkey.ROOMdatabase;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fusionkey.lowkey.chat.models.MessageTO;
import fusionkey.lowkey.models.UserD;


@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<UserD> getAll();

    @Query("SELECT * FROM user where name LIKE  :name ")
    UserD findByName(String name);

    @Query("SELECT COUNT(*) from user")
    int countUsers();

    @Insert
    void insertAll(UserD... users);

    @Delete
    void delete(UserD user);

    @Update
    void update(UserD...users);

    public class Converters {
        @TypeConverter
        public static ArrayList<MessageTO> stringToSomeObjectList(String value) {
            Type listType = new TypeToken<ArrayList<MessageTO>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public static String someObjectListToString(ArrayList<MessageTO> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }
}