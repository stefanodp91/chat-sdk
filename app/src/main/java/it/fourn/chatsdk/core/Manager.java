package it.fourn.chatsdk.core;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

public abstract class Manager implements Facade {
    private Context mContext;
    private FirebaseAuth mAuth;

    public Manager(Context context) {
        mContext = context;
    }

    public Manager(Context context, FirebaseAuth auth) {
        this(context);
        mAuth = auth;
    }

    public Context getContext() {
        return mContext;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    @Override
    public void dispose() {
        mContext = null;
        mAuth = null;
    }
}
