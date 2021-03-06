package it.fourn.chatsdk.core.conversations;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.exceptions.FirebaseUserNotValidException;
import it.fourn.chatsdk.core.exceptions.UnReadConversationsException;
import it.fourn.chatsdk.core.models.Conversation;
import it.fourn.chatsdk.core.utilities.Log;

class UnreadConversationsManager extends Manager {

    private static final String TAG = UnreadConversationsManager.class.getName();

    private String mUserId;
    private List<Conversation> mUnreadConversations;
    private ValueEventListener mUnreadConversationsListener;
    private DatabaseReference node;

    UnreadConversationsManager(Context context, FirebaseAuth auth) {
        super(context, auth);
        mUnreadConversations = new ArrayList<>();

        // retrieve user id
        mUserId = null;
        if (getAuth() != null && getAuth().getCurrentUser() != null && !getAuth().getCurrentUser().getUid().isEmpty()) {
            mUserId = getAuth().getCurrentUser().getUid();
        } else {
            if (getAuth() == null) {
                throw new FirebaseUserNotValidException("firebaseAuth cannot be null");
            } else if (getAuth().getCurrentUser() == null) {
                throw new FirebaseUserNotValidException("firebaseUser cannot be null");
            } else if (getAuth().getCurrentUser().getUid().isEmpty()) {
                throw new FirebaseUserNotValidException("firebaseUser id cannot be empty");
            } else {
                throw new FirebaseUserNotValidException("generic exception");
            }
        }

        // retrieve node reference
        node = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatManager.getInstance().getFirebaseUrl())
                .child(getContext().getString(R.string.firebase_node_unread_conversations, ChatManager.getInstance().getAppId(), mUserId));
        node.keepSynced(ChatManager.getInstance().isSynced());
        Log.d(TAG, "UnreadConversationsManager.databaseReference: " + node.toString());
    }

    Flowable<Integer> countUnreadConversations() {
        return Flowable.create(emitter -> {
            if (this.mUnreadConversationsListener == null) {
                // count the number of conversation unread
                mUnreadConversationsListener = node.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            try {
                                // decode the conversation
                                Conversation conversation = ConversationUtils.decodeConversationFromSnapshot(mUserId, postSnapshot);

                                // if the conversation is new add it to the unread conversations list
                                // otherwise remove it from the unread conversations list
                                if (conversation.isNew()) {
                                    if (!mUnreadConversations.contains(conversation)) {
                                        // add the conversation to the unread conversations list
                                        mUnreadConversations.add(conversation);
                                    } else {
                                        // update the conversation within the conversations list
                                        int index = mUnreadConversations.indexOf(conversation);
                                        mUnreadConversations.set(index, conversation);
                                    }
                                } else {
                                    // if the unread conversations list contains
                                    // the conversation remove it
                                    if (mUnreadConversations.contains(conversation)) {
                                        mUnreadConversations.remove(conversation);
                                    }
                                }

                                // notify to all subscribers that the unread conversations list changed
                                emitter.onNext(mUnreadConversations.size());
                            } catch (Exception e) {
                                // notify to all subscribers that an error occurred
                                if (!emitter.isCancelled())
                                    emitter.onError(new UnReadConversationsException(e));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // notify to all subscribers that an error occurred
                        if (!emitter.isCancelled())
                            emitter.onError(new UnReadConversationsException(databaseError));
                    }
                });
            }
            emitter.setCancellable(() -> node.removeEventListener(mUnreadConversationsListener));
            node.addValueEventListener(mUnreadConversationsListener);
        }, BackpressureStrategy.DROP);
    }

    @Override
    public void dispose() {
        mUserId = null;
        mUnreadConversations = null;
        mUnreadConversationsListener = null;
        node = null;

        super.dispose();
    }
}