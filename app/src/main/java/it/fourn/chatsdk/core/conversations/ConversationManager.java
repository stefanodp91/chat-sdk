package it.fourn.chatsdk.core.conversations;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.Flowable;
import it.fourn.chatsdk.core.Manager;

public class ConversationManager extends Manager {

    private Context mContext;
    private FirebaseAuth mAuth;
    private String mUserId;
    private UnreadConversationsManager mUnreadConversationsManager;

    public ConversationManager(Context context, FirebaseAuth auth) {
        mContext = context;
        mAuth = auth;
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            mUserId = mAuth.getCurrentUser().getUid();
        }
    }

    public Flowable<Integer> countUnreadConversations() {
        if (mUnreadConversationsManager == null) {
            mUnreadConversationsManager = new UnreadConversationsManager(mContext, mUserId);
        }
        return mUnreadConversationsManager.countUnreadConversations();
    }

    @Override
    public void dispose() {
        mContext = null;
        mAuth = null;

        if (mUnreadConversationsManager != null) {
            mUnreadConversationsManager.dispose();
        }
        mUnreadConversationsManager = null;
    }
}