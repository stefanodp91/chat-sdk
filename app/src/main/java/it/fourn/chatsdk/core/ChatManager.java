package it.fourn.chatsdk.core;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import it.fourn.chatsdk.core.authentication.AuthManager;
import it.fourn.chatsdk.core.rx.RxManager;

public class ChatManager {
    private static ChatManager ourInstance = new ChatManager();

    private Context mContext;
    private Configuration mConfiguration;
    private AuthManager mAuthManager;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    private ChatManager() {
    }

    /**
     * Initialize the chat manager
     *
     * @param context
     * @param configuration
     */
    public static void init(Context context, Configuration configuration) {
        ChatManager chatManager = new ChatManager(); // create chat manager
        ourInstance = chatManager;// update instance

        // TODO: 16/03/2019 decommentare
//        // This line needs to be executed before any usage of EmojiTextView, EmojiEditText or EmojiButton.
//        // EmojiManager.install(new IosEmojiProvider()); // apple icons
//        EmojiManager.install(new GoogleEmojiProvider()); // google icons

        chatManager.mContext = context;
        chatManager.mConfiguration = configuration;

        if (chatManager.mAuthManager == null) {
            chatManager.mAuthManager = new AuthManager(chatManager.mContext);
        }
    }

    /**
     * @param context
     * @param configuration
     * @param persistentOnDisk The Firebase Database client will cache synchronized data and keep
     *                         track of all writes you've initiated while your application is running.
     *                         It seamlessly handles intermittent network connections and re-sends
     *                         write operations when the network connection is restored.
     *                         However by default your write operations and cached data are
     *                         only stored in-memory and will be lost when your app restarts.
     *                         By setting this value to `true`, the data will be persisted to on-device (disk) storage and
     *                         will thus be available again when the app is restarted (even when there is no network connectivity at that time).
     *                         Note that this method must be called before creating your first Database reference and only needs to be called once per application.
     */
    public static void init(Context context, Configuration configuration, boolean persistentOnDisk) {
        //enable persistence must be made before any other usage of FirebaseDatabase instance.
        FirebaseDatabase.getInstance().setPersistenceEnabled(persistentOnDisk);
        init(context, configuration);
    }

    /**
     * Return the authentication manager object
     *
     * @return
     */
    public AuthManager getAuthManager() {
        return mAuthManager != null ? mAuthManager : new AuthManager(mContext);
    }

    /**
     * Return the appId from the configuration
     *
     * @return the appId
     */
    public String getAppId() {
        return mConfiguration.appId;
    }

    /**
     * Return the firebaseUrl from the configuration
     *
     * @return the firebaseUrl
     */
    public String getFirebaseUrl() {
        return mConfiguration.firebaseUrl;
    }

    /**
     * Return the application context
     *
     * @return the application context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Set up a set of instructions necessary for chat operation
     */
    public static final class Configuration {
        private String appId;
        private String firebaseUrl;
        private String storageBucket;

        Configuration(Builder builder) {
            appId = builder.mAppId;
            firebaseUrl = builder.mFirebaseUrl;
            storageBucket = builder.mStorageBucket;
        }

        public static final class Builder {
            private String mAppId;
            private String mFirebaseUrl;
            private String mStorageBucket;

            /**
             * Set the appId
             *
             * @param appId the unique identifier of the chat app on firebase
             */
            public Builder(String appId) {
                mAppId = appId;
            }

            /**
             * Set the firebaseUrl
             *
             * @param firebaseUrl the url of the chat app on firebase
             */
            public Builder firebaseUrl(String firebaseUrl) {
                mFirebaseUrl = firebaseUrl;
                return this;
            }

            /**
             * Set the storageBucket.
             * The storageBucket allows to save files on firebase.
             *
             * @param storageBucket the url of the storageBucket app on firebase
             */
            public Builder storageBucket(String storageBucket) {
                mStorageBucket = storageBucket;
                return this;
            }

            /**
             * Create an instance of the configuration object
             *
             * @return the instance of configuration
             */
            public Configuration build() {
                return new Configuration(this);
            }
        }
    }

    /**
     * Dispose all managers, disposables and clean resources.
     */
    public void dispose() {
        if (mAuthManager != null) {
            mAuthManager.dispose();
        }

        disposeAllDisposables();
    }

    // dispose all pending disposables
    private void disposeAllDisposables() {
        if (!RxManager.getInstance().getCompositeDisposable().isDisposed()) {
            RxManager.getInstance().getCompositeDisposable().dispose();
        }
    }
}