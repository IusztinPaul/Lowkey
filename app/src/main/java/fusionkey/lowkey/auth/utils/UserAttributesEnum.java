package fusionkey.lowkey.auth.utils;

public enum UserAttributesEnum {
    USERNAME("nickname"),
    PHONE("phone_number"),
    ADDRESS("address"),
    GENDER("gender"),
    BIRTH_DATE("birthdate"),
    EMAIL("email"),
    PASSWORD("password")
    ;

    private final String attribute;

    UserAttributesEnum(final String attribute) {
        this.attribute = attribute;
    }

    public String toString() {
        return attribute;
    }
}
