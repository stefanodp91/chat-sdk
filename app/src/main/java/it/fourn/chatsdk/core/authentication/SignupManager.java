package it.fourn.chatsdk.core.authentication;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.Signals;
import it.fourn.chatsdk.core.models.User;
import it.fourn.chatsdk.core.models.UserDTO;
import it.fourn.chatsdk.core.rx.RxBus;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;

class SignupManager extends Manager {

    private static final String TAG = SignupManager.class.getName();

    private Context mContext;
    private FirebaseAuth mAuth;

    SignupManager(Context context, FirebaseAuth auth) {
        mContext = context;
        mAuth = auth;
    }

    Single<User> signup(String email, String password, String firstName, String lastName) {
        return createUserWithEmailAndPassword(email, password)
                .flatMap(userId -> saveUserOnFirebase(userId, email, firstName, lastName));
    }

    // create a new user
    private Single<String> createUserWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            try {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            emitter.onSuccess(firebaseUser.getUid());
                        }
                    } else {
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_ERROR, task.getException()));
                        emitter.onError(task.getException());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "createUserWithEmail:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    // save a new user below the <b> contact </b> node.
    private Single<User> saveUserOnFirebase(String id, String email, String firstName, String lastName) {
        return Single.create(emitter -> {
            try {

                // firebase uploadable map
                final Map<String, Object> map = new HashMap<>();
                map.put(mContext.getString(R.string.signup_key_user_email), email);
                map.put(mContext.getString(R.string.signup_key_user_first_name), firstName);
                map.put(mContext.getString(R.string.signup_key_user_profile_picture), "");
                map.put(mContext.getString(R.string.signup_key_user_last_name), lastName);
                map.put(mContext.getString(R.string.signup_key_user_timestamp), new Date().getTime());
                map.put(mContext.getString(R.string.signup_key_user_id), id);

                DatabaseReference node = FirebaseDatabase.getInstance().getReference()
                        .child(mContext.getString(R.string.firebase_node_contacts, ChatManager.getInstance().getAppId()));

                // save the user on contacts node
                node.child(id).setValue(map, (databaseError, databaseReference) -> {
                    Log.d(TAG, "saveUserOnFirebase.databaseReference: " + databaseReference.toString());

                    if (databaseError == null) {
                        // create user
                        User user = new UserDTO();
                        user.setEmail(email);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setId(id);
                        Log.d(TAG, "saveUserOnFirebase:success");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_SUCCESS, user));
                        emitter.onSuccess(user);
                    } else {
                        Log.e(TAG, "saveUserOnFirebase:failure");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_ERROR, databaseError.toException().getCause()));
                        emitter.onError(databaseError.toException());
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "saveUserOnFirebase:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.CREATE_WITH_USERNAME_PASSWORD_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    @Override
    public void dispose() {
        mContext = null;
        mAuth = null;
    }
}