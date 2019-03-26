package it.fourn.chatsdk.core.exceptions;

import com.google.firebase.database.DatabaseError;

public class ConversationsListException extends Exception {

    public ConversationsListException(DatabaseError databaseError) {
        super(databaseError.toException());
    }

    public ConversationsListException(Exception e) {
        super(e);
    }

    /**
     * Constructs a <code>ConversationsListException</code> with
     * <code>null</code> as its error detail message.
     */
    public ConversationsListException() {
        super();
    }

    /**
     * Constructs a <code>ConversationsListException</code> with the
     * specified detail message. The string <code>s</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param s the detail message.
     */
    public ConversationsListException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>ConversationsListException</code> with a detail message
     * consisting of the given pathname string followed by the given reason
     * string.  If the <code>reason</code> argument is <code>null</code> then
     * it will be omitted.  This private constructor is invoked only by native
     * I/O methods.
     *
     * @since 1.2
     */
    private ConversationsListException(String path, String reason) {
        super(path + ((reason == null) ? "" : " (" + reason + ")"));
    }
}
