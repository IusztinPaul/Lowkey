package fusionkey.lowkey.main.utils;

public class EmailBuilder {

    public static String buildEmail(String emailToBuild){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(emailToBuild);
        stringBuilder.insert(stringBuilder.length()-3,'.');
        stringBuilder.insert(stringBuilder.length()-9,'@');
        return stringBuilder.toString();
    }

}
