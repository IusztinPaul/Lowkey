package fusionkey.lowkey.main.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import fusionkey.lowkey.LowKeyApplication;

public class PhotoUtils {

    private static final String PHOTOS_PATH_DIR = "photos" + File.separator;
    private static final String PROFILE_PHOTOS_DIR = "profile" + File.separator;

    private static String getFullPath() {
        return LowKeyApplication.instance.getApplicationContext().getFilesDir().getPath() + File.separator;
    }

    public static void saveProfilePhoto(Bitmap photo, String fileName) {
        savePhotoToFile(photo, PROFILE_PHOTOS_DIR + fileName);
    }

    private static void savePhotoToFile(Bitmap photo, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getFullPath() + PHOTOS_PATH_DIR + path);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getProfilePhoto(String fileName) {
        return getSavedPhoto(PROFILE_PHOTOS_DIR + fileName);
    }

    private static Bitmap getSavedPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(getFullPath() + PHOTOS_PATH_DIR + path, options);
    }
}
