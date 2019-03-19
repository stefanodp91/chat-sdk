package it.fourn.chatsdk.core.authentication;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.Signals;
import it.fourn.chatsdk.core.exceptions.FieldNotFoundException;
import it.fourn.chatsdk.core.models.User;
import it.fourn.chatsdk.core.models.UserDTO;
import it.fourn.chatsdk.core.rx.RxBus;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;

class LoginManager extends Manager {

    private static final String TAG = LoginManager.class.getName();

    private Context mContext;
    private FirebaseAuth mAuth;


    LoginManager(Context context, FirebaseAuth auth) {
        mContext = context;
        mAuth = auth;
    }

    Single<User> login(String email, String password) {
        return signInWithEmailAndPassword(email, password).flatMap(this::retrieveUserFromFirebase);
    }

    // retrieve an existing user
    private Single<FirebaseUser> signInWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            try {
                if (mAuth != null) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful() && mAuth != null) {
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            // listen for login changes
                            ChatManager.getInstance().getAuthManager().subscribeOnAuthStateListener(mAuth);
                            if (firebaseUser != null) {
                                emitter.onSuccess(firebaseUser);
                            }
                        } else {
                            Log.e(TAG, "signInWithEmailAndPassword:failure", task.getException());
                            RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_ERROR, task.getException()));
                            emitter.onError(task.getException());
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "signInWithEmailAndPassword:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    // retrieve an existing user below the <b> contact </b> node.
    private Single<User> retrieveUserFromFirebase(FirebaseUser firebaseUser) {
        return Single.create(emitter -> {
            try {
                DatabaseReference node = FirebaseDatabase.getInstance().getReference()
                        .child(mContext.getString(R.string.firebase_node_contact, ChatManager.getInstance().getAppId(), firebaseUser.getUid()));
                node.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            try {
                                User user = decodeContactSnapShop(dataSnapshot);
                                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_SUCCESS, user));
                                emitter.onSuccess(user);
                            } catch (FieldNotFoundException e) {
                                Log.e(TAG, "retrieveUserFromFirebase:failure");
                                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_ERROR, e));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "retrieveUserFromFirebase:failure");
                        RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_ERROR, databaseError.toException()));
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "retrieveUserFromFirebase:failure");
                RxManager.getInstance().getRxBus().send(new RxBus.RxBusEvent<>(Signals.LOGIN_WITH_USERNAME_PASSWORD_ERROR, e.getCause()));
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    // convert a snapshot to User model
    private User decodeContactSnapShop(DataSnapshot dataSnapshot) throws FieldNotFoundException {

        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

        if (map == null) {
            throw new FieldNotFoundException("Required map. Map is null for dataSnapshot : " + dataSnapshot);
        }

        String uid = (String) map.get("uid");
        if (uid == null) {
            throw new FieldNotFoundException("Required uid. Uid is null for dataSnapshot : " + dataSnapshot);
        }

        String lastname = (String) map.get("lastname");
        String firstname = (String) map.get("firstname");
//        String imageurl = (String) map.get("imageurl");
        String email = (String) map.get("email");

        User contact = new UserDTO();
        contact.setId(uid);
        contact.setEmail(email);
        contact.setFirstName(firstname);
        contact.setLastName(lastname);
        contact.setUsername(firstname + lastname);

        return contact;
    }

    @Override
    public void dispose() {
        mContext = null;
        mAuth = null;
    }
}
