package it.fourn.chatsdk.core.exceptions;

public class FirebaseUserNotValidException extends RuntimeException {
    private static final long serialVersionUID = -897856973823710492L;

    /**
     * Constructs a <code>FirebaseUserNotValidException</code> with
     * <code>null</code> as its error detail message.
     */
    public FirebaseUserNotValidException() {
        super();
    }

    /**
     * Constructs a <code>FirebaseUserNotValidException</code> with the
     * specified detail message. The string <code>s</code> can be
     * retrieved later by the
     * <code>{@link Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param s the detail message.
     */
    public FirebaseUserNotValidException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>FirebaseUserNotValidException</code> with a detail message
     * consisting of the given pathname string followed by the given reason
     * string.  If the <code>reason</code> argument is <code>null</code> then
     * it will be omitted.  This private constructor is invoked only by native
     * I/O methods.
     *
     * @since 1.2
     */
    private FirebaseUserNotValidException(String path, String reason) {
        super(path + ((reason == null) ? "" : " (" + reason + ")"));
    }
}
