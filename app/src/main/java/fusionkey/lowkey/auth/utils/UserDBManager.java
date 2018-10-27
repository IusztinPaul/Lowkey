package fusionkey.lowkey.auth.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import fusionkey.lowkey.auth.models.UserDB;

public class UserDBManager {
    private static DynamoDBMapper dynamoDBMapper;

    public static void create(final String userEmail,
                              final String birthDate,
                              final String gender,
                              final String fullName,
                              final String username,
                              final String phone) {
        create(userEmail, birthDate, gender, fullName, username, phone, 0L, new ArrayList<Long>());
    }

    public static void create(final String userEmail, final String userUsername) {
        create(userEmail, null, null, null, userUsername, null);
    }

    public static void create(final String userEmail,
                              final String birthDate,
                              final String gender,
                              final String fullName,
                              final String username,
                              final String phone,
                              final long score,
                              final List<Long> timeStamps) {
        final UserDB userDB = new UserDB(userEmail, birthDate, gender,
                fullName, username, phone, score, timeStamps);

        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(userDB);
            }
        }).start();
    }

    public static void update(final UserDB userDB) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

       new Thread(new Runnable() {
           @Override
           public void run() {
               dynamoDBMapper.save(userDB,
                       new DynamoDBMapperConfig(
                               DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
               );
           }
       }).start();
    }

    public static void update(Map<UserAttributesEnum, String> attributes) {
        UserDB userDB = createUserDBFromMap(attributes);
        update(userDB);
    }

    public static void delete(final String userEmail) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDB userDB = new UserDB(userEmail);
                dynamoDBMapper.delete(userDB);
            }
        }).start();
    }

    public static UserDB getUserData(final String userEmail) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        Callable<UserDB> callable = new Callable<UserDB>() {
            @Override
            public UserDB call() throws Exception {
                return dynamoDBMapper.load(UserDB.class, userEmail);
            }
        };
        return runInBackground(callable);
    }

    public static UserDB createUserDBFromMap(Map<UserAttributesEnum, String> attributes) {
        String userEmail = attributes.get(UserAttributesEnum.EMAIL);
        String birthDate = attributes.get(UserAttributesEnum.BIRTH_DATE);
        String gender = attributes.get(UserAttributesEnum.GENDER);
        String fullName = attributes.get(UserAttributesEnum.FULL_NAME);
        String username = attributes.get(UserAttributesEnum.USERNAME);
        String phone = attributes.get(UserAttributesEnum.PHONE);

        return new UserDB(userEmail, birthDate, gender, fullName, username, phone);
    }

    private static UserDB runInBackground(Callable<UserDB> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<UserDB> futureTask = new FutureTask<>(callable);
        try {
            executor.submit(futureTask);
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        } finally {
            executor.shutdown();
        }
    }

    private static DynamoDBMapper createDynamoDBMapper() {
        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();


        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        return DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();
    }
}
