package it.fourn.chatsdk.core;

import android.content.Context;

import com.google.firebase.FirebaseApp;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import it.fourn.chatsdk.R;

public class ChatApplication extends MultiDexApplication {

    private static final ChatApplication ourInstance = new ChatApplication();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static ChatApplication getInstance() {
        return ourInstance;
    }

    public ChatApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initializes the default FirebaseApp instance using string resource values - populated from google-services.json.
        // It also initializes Firebase Analytics for the current process.
        // This method is called at app startup time by FirebaseInitProvider.
        // Call this method before any Firebase APIs in components outside the main process.
        // The FirebaseOptions  values used by the default app instance are read from string resources.
        FirebaseApp.initializeApp(this);

        // Create the minimum set of configuration to run the chat sdk
        ChatManager.Configuration configuration = new ChatManager.Configuration.Builder(getString(R.string.configuration_app_id)) // set app id
                .firebaseUrl(getString(R.string.configuration_firebase_url)) // set firebase url
//                .storageBucket(getString(R.string.configuration_firebase_storage_bucket)) // set storage bucket
                .build();
        ChatManager.init(this, configuration, false);
    }
}
