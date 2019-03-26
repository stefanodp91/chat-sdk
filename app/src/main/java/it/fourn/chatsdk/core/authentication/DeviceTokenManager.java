package it.fourn.chatsdk.core.authentication;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.Signals;
import it.fourn.chatsdk.core.rx.RxBus;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;
import it.fourn.chatsdk.core.utilities.ThisDevice;

class DeviceTokenManager extends Manager {

    private static final String TAG = DeviceTokenManager.class.getName();

    DeviceTokenManager(Context context, FirebaseAuth auth) {
        super(context, auth);
    }

    Single<String> getDeviceTokenAndUpdateNodeReference() {
        return getDeviceToken().flatMap(this::saveFirebaseInstance);
    }

    Single<String> getDeviceToken() {

        return Single.create(emitter -> {
            try {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "getDeviceToken:failure");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.GET_DEVICE_ID_SUCCESS, task.getException()));
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.GET_DEVICE_ID_SUCCESS, token));
                    emitter.onSuccess(token);
                });
            } catch (Exception e) {
                Log.e(TAG, "getDeviceToken:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.GET_DEVICE_ID_SUCCESS, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    // save the device token to firebase
    Single<String> saveFirebaseInstance(String token) {

        return Single.create(emitter -> {

            FirebaseUser firebaseUser = getAuth().getCurrentUser();
            String appId = ChatManager.getInstance().getAppId();

            try {
                if (firebaseUser != null && appId != null && !appId.isEmpty()) {
                    DatabaseReference node = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatManager.getInstance().getFirebaseUrl())
                            .child(getContext().getString(R.string.firebase_node_user_instance_id, appId, firebaseUser.getUid(), token));

                    // return device info
                    Map<String, Object> device = new HashMap<>();
                    device.put("manufacturer", ThisDevice.thisDevice().getManufacturer());
                    device.put("model", ThisDevice.thisDevice().getModel());
                    device.put("platform", "Android");
                    device.put("platform_version", ThisDevice.thisDevice().getVersion());
                    device.put("language", getContext().getResources().getConfiguration().locale.toString());
                    device.put("last_app_launched", new Date().getTime());

                    // upload data
                    node.setValue(device, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Log.d(TAG, "saveFirebaseInstance.databaseReference: " + databaseReference.toString());
                            emitter.onSuccess(token);
                        } else {
                            RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.GET_DEVICE_ID_ERROR, databaseError.toException()));
                            Log.e(TAG, "saveFirebaseInstance: cannot save token: " + token + " for user with uid: " + firebaseUser.getUid());
                            if (!emitter.isDisposed())
                                emitter.onError(new Throwable("saveFirebaseInstance: cannot save token: " + token + " for user with uid: " + firebaseUser.getUid()));
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "saveFirebaseInstance: cannot save token: " + token + " for user with uid: " + firebaseUser.getUid());
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }
}
