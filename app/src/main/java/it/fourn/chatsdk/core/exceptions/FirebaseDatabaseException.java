package it.fourn.chatsdk.core.exceptions;

import com.google.firebase.database.DatabaseError;

public class FirebaseDatabaseException extends Exception {

    public FirebaseDatabaseException(DatabaseError databaseError) {
        super(databaseError.toException());
    }

    public FirebaseDatabaseException(Exception e) {
        super(e);
    }

    /**
     * Constructs a <code>FirebaseDatabaseException</code> with
     * <code>null</code> as its error detail message.
     */
    public FirebaseDatabaseException() {
        super();
    }

    /**
     * Constructs a <code>FirebaseDatabaseException</code> with the
     * specified detail message. The string <code>s</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param s the detail message.
     */
    public FirebaseDatabaseException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>FirebaseDatabaseException</code> with a detail message
     * consisting of the given pathname string followed by the given reason
     * string.  If the <code>reason</code> argument is <code>null</code> then
     * it will be omitted.  This private constructor is invoked only by native
     * I/O methods.
     *
     * @since 1.2
     */
    private FirebaseDatabaseException(String path, String reason) {
        super(path + ((reason == null) ? "" : " (" + reason + ")"));
    }
}
