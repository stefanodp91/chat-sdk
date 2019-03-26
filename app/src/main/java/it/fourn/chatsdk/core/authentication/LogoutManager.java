package it.fourn.chatsdk.core.authentication;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import io.reactivex.Single;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.Signals;
import it.fourn.chatsdk.core.rx.RxBus;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;

class LogoutManager extends Manager {

    private static final String TAG = LogoutManager.class.getName();

    private FirebaseUser mFirebaseUser;

    LogoutManager(Context context, FirebaseAuth auth) {
        super(context, auth);
        mFirebaseUser = getAuth().getCurrentUser();
    }

    // retrieve the device token and perform logout
    Single<String> logout() {
        return ChatManager.getInstance().getAuthManager().getDeviceTokenManager().getDeviceToken().flatMap(this::performLogout);
    }

    // logout and update device token
    private Single<String> performLogout(String token) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException ignore) {
        }

        return Single.create(emitter -> {
            try {
                // remove the instanceId for the logged user
                DatabaseReference node = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatManager.getInstance().getFirebaseUrl())
                        .child(getContext().getString(R.string.firebase_node_user_instance_id, ChatManager.getInstance().getAppId(),
                                mFirebaseUser.getUid(), token));
                node.removeValue((databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        Log.d(TAG, "performLogout:success");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGOUT_SUCCESS, token));
                        // listen for login changes
                        ChatManager.getInstance().getAuthManager().unsubscribeOnAuthStateListener(getAuth());
                        emitter.onSuccess(token);
                    } else {
                        Log.e(TAG, "performLogout:failure");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGOUT_ERROR, databaseError.toException()));
                        if (!emitter.isDisposed())
                            emitter.onError(databaseError.toException());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "performLogout:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGOUT_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }
}
