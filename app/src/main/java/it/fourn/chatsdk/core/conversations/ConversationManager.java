package it.fourn.chatsdk.core.conversations;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.Flowable;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.models.Conversation;

public class ConversationManager extends Manager {

    private UnreadConversationsManager mUnreadConversationsManager;
    private ConversationsListManager mConversationsListManager;

    public ConversationManager(Context context, FirebaseAuth auth) {
        super(context, auth);
    }

    public Flowable<Integer> countUnreadConversations() {
        if (mUnreadConversationsManager == null) {
            mUnreadConversationsManager = new UnreadConversationsManager(getContext(), getAuth());
        }
        return mUnreadConversationsManager.countUnreadConversations();
    }

    public Flowable<Conversation> subscribeOnConversationsUpdates() {
        if (mConversationsListManager == null) {
            mConversationsListManager = new ConversationsListManager(getContext(), getAuth());
        }
        return mConversationsListManager.subscribeOnConversationsUpdates();
    }

    @Override
    public void dispose() {
        // unread conversations
        if (mUnreadConversationsManager != null) {
            mUnreadConversationsManager.dispose();
        }
        mUnreadConversationsManager = null;

        // conversations list
        if (mConversationsListManager != null) {
            mConversationsListManager.dispose();
        }
        mConversationsListManager = null;

        super.dispose();
    }
}