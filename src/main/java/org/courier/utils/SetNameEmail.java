package org.courier.utils;

public class SetNameEmail {
    private static String userName;
    private static String email;

    public SetNameEmail(String userName, String email) {
        SetNameEmail.userName = userName;
        SetNameEmail.email = email;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        SetNameEmail.email = email;
    }

    public void setUserName(String userName) {
        SetNameEmail.userName = userName;
    }

}
