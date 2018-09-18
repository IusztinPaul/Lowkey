package fusionkey.lowkey.auth.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import fusionkey.lowkey.models.UserDB;

public class UserDBManager {
    // Attributes
    static final String USER_ID = "userId";
    static final String SCORE = "score";

    private static DynamoDBMapper dynamoDBMapper;

    public static void create(final String userId) {
        final UserDB userDB = new UserDB();
        userDB.setUserId(userId);
        userDB.setScore(0L);

        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
               dynamoDBMapper.save(userDB);
            }
        }).start();
    }

    public static void update(final String userId, final long score) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

       new Thread(new Runnable() {
           @Override
           public void run() {
               UserDB userDB = new UserDB();
               userDB.setUserId(userId);
               userDB.setScore(score);
               dynamoDBMapper.save(userDB, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
           }
       });
    }

    public static void delete(final String userId) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDB userDB = new UserDB();
                userDB.setUserId(userId);
                dynamoDBMapper.delete(userDB);
            }
        });
    }

    public static UserDB getUserData(final String userId) {
        if(dynamoDBMapper == null)
            dynamoDBMapper = createDynamoDBMapper();

        Callable<UserDB> callable = new Callable<UserDB>() {
            @Override
            public UserDB call() throws Exception {
                return dynamoDBMapper.load(UserDB.class, userId);
            }
        };
        return runInBackground(callable);
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
