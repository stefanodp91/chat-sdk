package it.fourn.chatsdk.core.authentication;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.Signals;
import it.fourn.chatsdk.core.models.User;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;

public class AuthManager extends Manager {
    private static final String TAG = AuthManager.class.getName();

    private FirebaseAuth mAuth;
    private SignupManager mSignupManager;
    private LoginManager mLoginManager;
    private LogoutManager mLogoutManager;
    private DeviceTokenManager mDeviceTokenManager;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public AuthManager(Context context) {
        super(context);
        mAuth = FirebaseAuth.getInstance();

        // save the device token for the new user
        RxManager.getInstance().getCompositeDisposable().add(RxManager.getInstance().getRxBus()
                .subscribeToBus(Signals.CREATE_WITH_USERNAME_PASSWORD_SUCCESS,
                        user -> getDeviceTokenManager().getDeviceTokenAndUpdateNodeReference()
                                .subscribeOn(RxManager.getInstance().getSchedulerProvider().io())
                                .observeOn(RxManager.getInstance().getSchedulerProvider().ui())
                                .subscribe(token -> Log.d(TAG, "CREATE_WITH_USERNAME_PASSWORD_SUCCESS:token: " + token),
                                        throwable -> {
                                            if (throwable != null) {
                                                Log.e(TAG, throwable);
                                            }
                                        })));

        // update the device token for the existing user
        RxManager.getInstance().getCompositeDisposable().add(RxManager.getInstance().getRxBus()
                .subscribeToBus(Signals.LOGIN_WITH_USERNAME_PASSWORD_SUCCESS,
                        user -> {
                            Log.d(TAG, "LOGIN_WITH_USERNAME_PASSWORD_SUCCESS:token: " + user);
                            getDeviceTokenManager().getDeviceTokenAndUpdateNodeReference()
                                    .subscribeOn(RxManager.getInstance().getSchedulerProvider().io())
                                    .observeOn(RxManager.getInstance().getSchedulerProvider().ui())
                                    .subscribe(token -> Log.d(TAG, "LOGIN_WITH_USERNAME_PASSWORD_SUCCESS:token: " + token),
                                            throwable -> {
                                                if (throwable != null) {
                                                    Log.e(TAG, throwable);
                                                }
                                            });
                        }));
    }

    /**
     * Dispose the auth manager and clean resources.
     */
    @Override
    public void dispose() {
        if (mSignupManager != null) {
            mSignupManager.dispose();
        }
        mSignupManager = null;

        if (mLoginManager != null) {
            mLoginManager.dispose();
        }
        mLoginManager = null;

        if (mLogoutManager != null) {
            mLogoutManager.dispose();
        }
        mLogoutManager = null;

        if (mDeviceTokenManager != null) {
            mDeviceTokenManager.dispose();
        }
        mDeviceTokenManager = null;

        if (mAuth != null && mAuthListener != null) {
            unsubscribeOnAuthStateListener(mAuth);
        }
        mAuthListener = null;
        mAuth = null;
    }

    /**
     * Create a new user.
     *
     * @param email     new user email
     * @param password  new user password
     * @param firstName new user first name
     * @param lastName  new user last name
     */
    public Single<User> signup(String email, String password, String firstName, String lastName) {
        if (mSignupManager == null) {
            mSignupManager = new SignupManager(getContext(), mAuth);
        }

        return mSignupManager.signup(email, password, firstName, lastName);
    }

    /**
     * Login an existing user
     *
     * @param email    the user email
     * @param password the user password
     * @return the {@link #(SingleObserver)} method) to login an user
     */
    public Single<User> login(String email, String password) {
        if (mLoginManager == null) {
            mLoginManager = new LoginManager(getContext(), mAuth);
        }
        return mLoginManager.login(email, password);
    }

    /**
     * Logout of the logged user
     *
     * @return the {@link #(SingleObserver)} method) to logout the logged user
     */
    public Single<String> logout() {
        if (mLogoutManager == null) {
            mLogoutManager = new LogoutManager(getContext(), mAuth);
        }
        return mLogoutManager.logout();
    }

    // return the instance of the device token manager.
    // if it not exists create a new one
    DeviceTokenManager getDeviceTokenManager() {
        if (mDeviceTokenManager == null) {
            mDeviceTokenManager = new DeviceTokenManager(getContext(), mAuth);
        }
        return mDeviceTokenManager;
    }

    // listen for user login state changes
    void subscribeOnAuthStateListener(FirebaseAuth auth) {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "subscribeOnAuthStateListener: signed in " + auth);
            } else {
                // User is signed out
                Log.d(TAG, "subscribeOnAuthStateListener: signed out " + auth);
            }
        };

        if (auth != null) {
            auth.addAuthStateListener(mAuthListener);
        }
    }

    // stop listening for user login state changes
    void unsubscribeOnAuthStateListener(FirebaseAuth auth) {
        if (auth != null && mAuthListener != null) {
            Log.d(TAG, "unsubscribeOnAuthStateListener: signed out" + auth);
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }
}
