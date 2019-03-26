package it.fourn.chatsdk.core.conversations;

import android.content.Context;

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
import it.fourn.chatsdk.core.exceptions.FirebaseDatabaseException;
import it.fourn.chatsdk.core.models.Conversation;
import it.fourn.chatsdk.core.utilities.Log;

class UnreadConversationsManager extends Manager {

    private static final String TAG = UnreadConversationsManager.class.getName();

    private Context mContext;
    private String mUserId;
    private List<Conversation> mUnreadConversations;
    private ValueEventListener mUnreadConversationsListener;
    private DatabaseReference node;

    UnreadConversationsManager(Context context, String userId) {
        mContext = context;
        mUserId = userId;
        mUnreadConversations = new ArrayList<>();

        node = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatManager.getInstance().getFirebaseUrl())
                .child(mContext.getString(R.string.firebase_node_unread_conversations, ChatManager.getInstance().getAppId(), userId));
        node.keepSynced(true);
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
                                    emitter.onError(new FirebaseDatabaseException(e));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // notify to all subscribers that an error occurred
                        if (!emitter.isCancelled())
                            emitter.onError(new FirebaseDatabaseException(databaseError));
                    }
                });
            }
            emitter.setCancellable(() -> node.removeEventListener(mUnreadConversationsListener));
            node.addValueEventListener(mUnreadConversationsListener);
        }, BackpressureStrategy.DROP);
    }

    @Override
    public void dispose() {
        mContext = null;
        mUserId = null;
        mUnreadConversations = null;
    }
}