package utils;
import entities.user;


public class Session {
    private static user currentUser;

    public static user getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(user user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }
}