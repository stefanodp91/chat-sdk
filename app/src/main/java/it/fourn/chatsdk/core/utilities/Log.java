package it.fourn.chatsdk.core.utilities;

public class Log {

    public static void d(String tag, String message) {
        android.util.Log.d(tag, message);
    }

    public static void w(String tag, String message, Exception exception) {
        android.util.Log.w(tag, message, exception);
    }

    public static void e(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    public static void e(String tag, Throwable throwable) {
        android.util.Log.e(tag, throwable.toString());
    }

    public static void e(String tag, String message, Exception exception) {
        android.util.Log.e(tag, message, exception);
    }
}
