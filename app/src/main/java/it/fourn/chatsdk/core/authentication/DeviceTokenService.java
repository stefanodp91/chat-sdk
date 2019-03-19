package it.fourn.chatsdk.core.authentication;

import com.google.firebase.messaging.FirebaseMessagingService;

import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.utilities.Log;

public class DeviceTokenService extends FirebaseMessagingService {

    private static final String TAG = DeviceTokenService.class.getName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        ChatManager.getInstance().getAuthManager().getDeviceTokenManager().saveFirebaseInstance(token);
    }
}
