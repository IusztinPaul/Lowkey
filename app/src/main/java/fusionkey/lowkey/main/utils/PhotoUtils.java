package fusionkey.lowkey.main.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.Telephony;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import fusionkey.lowkey.LowKeyApplication;

public class PhotoUtils {
    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 3;

    private static final float PHOTO_SCALE_RATIO_BIG = 0.07f;
    private static final float PHOTO_SCALE_RATIO_SMALL = 0.2f;
    private static final int PHOTO_THRESHOLD = 2000;

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

    public static Bitmap resizeBitmap(Bitmap bitmap, int mode) {
        int width = scaleValue(bitmap.getWidth(), bitmap),
                height = scaleValue(bitmap.getHeight(), bitmap);

        if (mode > LARGE)
            mode = LARGE;
        else if(mode < SMALL)
            mode = SMALL;

        width *= mode;
        height *= mode;

        bitmap = Bitmap.createScaledBitmap(bitmap,
                width,
                height,
                true);

        return bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        return resizeBitmap(bitmap, SMALL);
    }

    public static int scaleValue(int value, Bitmap photo) {
        if (photo.getWidth() >= PHOTO_THRESHOLD || photo.getHeight() >= PHOTO_THRESHOLD)
            return Math.round(PHOTO_SCALE_RATIO_BIG * value);
        else
            return Math.round(PHOTO_SCALE_RATIO_SMALL * value);
    }

    public static Bitmap rotateBitmap90DegreesIfWidthBigger(Bitmap source) {
        if(source.getWidth() > source.getHeight())
            return rotateBitmap(source, -90);

        return source;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
