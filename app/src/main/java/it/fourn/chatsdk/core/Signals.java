package it.fourn.chatsdk.core;

public class Signals {
    /**
     * Signal sent when an user has been signed up with success
     */
    public static final String CREATE_WITH_USERNAME_PASSWORD_SUCCESS = "CREATE_WITH_USERNAME_PASSWORD_SUCCESS";

    /**
     * Signal sent when an user tried to sign up but the operation failed
     */
    public static final String CREATE_WITH_USERNAME_PASSWORD_ERROR = "CREATE_WITH_USERNAME_PASSWORD_ERROR";

    /**
     * Signal sent when an user has been logged in with success
     */
    public static final String LOGIN_WITH_USERNAME_PASSWORD_SUCCESS = "LOGIN_WITH_USERNAME_PASSWORD_SUCCESS";

    /**
     * Signal sent when an user tried to login but the operation failed
     */
    public static final String LOGIN_WITH_USERNAME_PASSWORD_ERROR = "LOGIN_WITH_USERNAME_PASSWORD_ERROR";

    /**
     * Signal sent when an user has been logged out with success
     */
    public static final String LOGOUT_SUCCESS = "LOGOUT_SUCCESS";

    /**
     * Signal sent when an user tried to logout but the operation failed
     */
    public static final String LOGOUT_ERROR = "LOGOUT_ERROR";

    /**
     * Signal sent when a device id has been saved with success
     */
    public static final String GET_DEVICE_ID_SUCCESS = "GET_DEVICE_ID_SUCCESS";

    /**
     * Signal sent when it tried to save the device ID but the operation failed
     */
    public static final String GET_DEVICE_ID_ERROR = "GET_DEVICE_ID_ERROR";

    /**
     * Signal sent when it tried count the unread conversations with success
     */
    public static final String UNREAD_CONVERSATIONS_SUCCESS = "UNREAD_CONVERSATIONS_SUCCESS";

    /**
     * Signal sent when it tried count the unread conversations but the operation failed
     */
    public static final String UNREAD_CONVERSATIONS_ERROR = "UNREAD_CONVERSATIONS_ERROR";
}
