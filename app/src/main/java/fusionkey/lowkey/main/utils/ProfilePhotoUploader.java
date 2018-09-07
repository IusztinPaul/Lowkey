package fusionkey.lowkey.main.utils;

import android.graphics.Bitmap;

import java.io.File;

public class ProfilePhotoUploader extends PhotoUploader {
    private final String PROFILE_NAME_FOLDER = "profile" + File.separator;

    public ProfilePhotoUploader(Bitmap photo) {
        super(photo);
    }

    public ProfilePhotoUploader() {
        super();
    }

    @Override
    public void upload(String fileName, Callback successCallback, Callback failCallback) {
        String path = PROFILE_NAME_FOLDER + fileName;
        super.upload(path, successCallback, failCallback);
    }

    @Override
    public void download(String fileName, Callback successCallback) {
        String path = PROFILE_NAME_FOLDER + fileName;
        super.download(path, successCallback);
    }
}
