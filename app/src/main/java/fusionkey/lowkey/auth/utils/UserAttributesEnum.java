package fusionkey.lowkey.auth.utils;

public enum UserAttributesEnum {
    USERNAME("username"),
    PHONE("phone"),
    ADDRESS("address"),
    GENDER("gender"),
    BIRTH_DATE("birthDate"),
    EMAIL("email"),
    FULL_NAME("fullName"),
    PICTURE("picture"),
    SCORE("score"),
    TIME_STAMPS("timeStamps")
    ;

    private final String attribute;

    UserAttributesEnum(final String attribute) {
        this.attribute = attribute;
    }

    public String toString() {
        return attribute;
    }
}
