package fusionkey.lowkey.main.profile;

import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.utils.AwsAccessKeys;

public class PhotoTransferer {

    private AmazonS3Client s3Client;
    private final String PICTURE_NAME_FILE = "picture";
    private final String PICTURES_NAME_FOLDER = "pictures" + File.separator;
    private final String PROFILE_NAME_FOLDER = "profile" + File.separator;
    private final String OTHER_NAME_FOLDER = "other" + File.separator;

    public PhotoTransferer() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(
                        AwsAccessKeys.ACCESS_KEY_ID,
                        AwsAccessKeys.ACCESS_SECRET_KEY
                );
        s3Client = new AmazonS3Client(credentials);
    }


    public void upload(String dataToFile, String fileName, boolean isProfile) {
        String path = "";
        if(isProfile)
            path += PROFILE_NAME_FOLDER;
        else
            path += OTHER_NAME_FOLDER;
        path += fileName;

        upload(dataToFile, path);
    }

    public void upload(String dataToFile, String path) {

        try {
            File file = LowKeyApplication.instance.getApplicationContext().getFilesDir();
            file = new File(file, PICTURE_NAME_FILE);

            // Write data to file.
            PrintWriter output = new PrintWriter(file);
            output.write(dataToFile);
            output.close();

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(LowKeyApplication.instance.getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .defaultBucket("lowkey-userfiles-mobilehub-1217601830")
                            .s3Client(s3Client)
                            .build();

            TransferObserver uploadObserver =
                    transferUtility.upload(PICTURES_NAME_FOLDER + path, file);

            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        // Handle a completed download.
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                }

                @Override
                public void onError(int id, Exception ex) {
                    // Handle errors
                }

            });

            // If your upload does not trigger the onStateChanged method inside your
            // TransferListener, you can directly check the transfer state as shown here.
            if (TransferState.COMPLETED == uploadObserver.getState()) {
                // Handle a completed upload.
            }
        } catch (IOException e) {
            Log.e("upload", e.getMessage());
        }
    }
}
