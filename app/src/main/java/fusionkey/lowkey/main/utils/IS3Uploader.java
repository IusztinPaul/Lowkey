package fusionkey.lowkey.main.utils;

public interface IS3Uploader {
    String BUCKET = "lowkey-userfiles-mobilehub-1217601830";
    void upload(String path, Callback successCallback, Callback failCallback);
    void download(String path, Callback successCallback);
}
