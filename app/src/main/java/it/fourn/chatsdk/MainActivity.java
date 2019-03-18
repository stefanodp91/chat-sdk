package it.fourn.chatsdk;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import it.fourn.chatsdk.core.authentication.AuthManager;
import it.fourn.chatsdk.core.rx.RxManager;
import it.fourn.chatsdk.core.utilities.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

//        // signup
//        RxManager.getInstance().getCompositeDisposable().add(AuthManager.getInstance()
//                .signup("jake.waerton@gmail.com", "123456", "jake", "waerton")
//                .subscribeOn(RxManager.getInstance().getSchedulerProvider().io())
//                .observeOn(RxManager.getInstance().getSchedulerProvider().ui())
//                .subscribe(user -> {
//                    if (user != null) {
//                        Toast.makeText(mContext, user.getId(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(mContext, "user is null", Toast.LENGTH_SHORT).show();
//                    }
//                }, throwable -> {
//                    if (throwable != null) {
//                        Log.e(TAG, throwable);
//                    }
//                }));
//
//        // login
//        RxManager.getInstance().getCompositeDisposable().add(AuthManager.getInstance()
//                .login("jake.waerton@gmail.com", "123456")
//                .subscribeOn(RxManager.getInstance().getSchedulerProvider().io())
//                .observeOn(RxManager.getInstance().getSchedulerProvider().ui())
//                .subscribe(user -> {
//                    if (user != null) {
//                        Toast.makeText(mContext, user.getId(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(mContext, "user is null", Toast.LENGTH_SHORT).show();
//                    }
//                }, throwable -> {
//                    if (throwable != null) {
//                        Log.e(TAG, throwable);
//                    }
//                }));

        // logout
        RxManager.getInstance().getCompositeDisposable().add(AuthManager.getInstance()
                .logout()
                .subscribeOn(RxManager.getInstance().getSchedulerProvider().io())
                .observeOn(RxManager.getInstance().getSchedulerProvider().ui())
                .subscribe(userId -> {
                    if (userId != null && !userId.isEmpty()) {
                        Toast.makeText(mContext, "user with uid: " + userId + " logged out with success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "user with uid: " + userId + " logged out with success");
                    }
                }, throwable -> {
                    if (throwable != null) {
                        Log.e(TAG, throwable);
                    }
                }));
    }
}