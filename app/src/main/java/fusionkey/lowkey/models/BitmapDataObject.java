package fusionkey.lowkey.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapDataObject {
    private String serializedImage;
    private Bitmap currentImage;

    public BitmapDataObject(String serializedImage) {
        this.serializedImage = serializedImage;
    }

    public BitmapDataObject(Bitmap currentImage) {
        this.currentImage = currentImage;
    }

    public void serialize(){
        if(currentImage == null)
            throw new RuntimeException("There is no image to serialize");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        serializedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void unserialize(){
        if(serializedImage == null)
            throw new RuntimeException("There is no image to deserialize");

        byte[] byteArray = Base64.decode(serializedImage, Base64.DEFAULT);
        currentImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public String getSerializedImage() {
        return serializedImage;
    }

    public Bitmap getCurrentImage() {
        return currentImage;
    }
}
