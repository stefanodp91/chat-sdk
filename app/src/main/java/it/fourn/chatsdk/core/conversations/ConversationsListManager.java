package it.fourn.chatsdk.core.conversations;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import it.fourn.chatsdk.R;
import it.fourn.chatsdk.core.ChatManager;
import it.fourn.chatsdk.core.Manager;
import it.fourn.chatsdk.core.exceptions.ConversationsListException;
import it.fourn.chatsdk.core.exceptions.FirebaseUserNotValidException;
import it.fourn.chatsdk.core.models.Conversation;

class ConversationsListManager extends Manager {

    private static final String TAG = ConversationsListManager.class.getName();

    private String mUserId;
    private List<Conversation> mConversations;
    private Comparator<Conversation> timestampComparator;
    private ChildEventListener mConversationsListener;
    private DatabaseReference node;

    ConversationsListManager(Context context, FirebaseAuth auth) {
        super(context, auth);

        mConversations = new ArrayList<>();

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
                .child(getContext().getString(R.string.firebase_node_conversations, ChatManager.getInstance().getAppId(), mUserId));
        node.keepSynced(ChatManager.getInstance().isSynced());
        it.fourn.chatsdk.core.utilities.Log.d(TAG, "ConversationsListManager.databaseReference: " + node.toString());

        // compare conversations object by timestamp
        timestampComparator = (o1, o2) -> {
            try {
                return o2.getTimestampLong().compareTo(o1.getTimestampLong());
            } catch (Exception e) {
                Log.e(TAG, "ConversationsListManager: cannot compare conversations timestamp", e);
                return 0;
            }
        };
    }

    Flowable<Conversation> subscribeOnConversationsUpdates() {
        return Flowable.create(emitter -> {
            if (this.mConversationsListener == null) {
                // count the number of conversation unread
                mConversationsListener = node.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            Conversation conversation = ConversationUtils.decodeConversationFromSnapshot(mUserId, dataSnapshot);

                            // it sets the conversation as read if the person whom are talking to is the current user
                            if (mUserId.equals(conversation.getSender())) {
                                setConversationRead(conversation.getConversationId());
                            }

                            ConversationUtils.saveOrUpdateConversationInMemory(mConversations, conversation);
                            ConversationUtils.sortConversationsInMemory(mConversations, timestampComparator);
                            // notify to all subscribers that the unread conversations list changed
                            emitter.onNext(conversation);
                        } catch (Exception e) {
                            // notify to all subscribers that an error occurred
                            if (!emitter.isCancelled())
                                emitter.onError(new ConversationsListException(e));
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            Conversation conversation = ConversationUtils.decodeConversationFromSnapshot(mUserId, dataSnapshot);
                            ConversationUtils.saveOrUpdateConversationInMemory(mConversations, conversation);
                            ConversationUtils.sortConversationsInMemory(mConversations, timestampComparator);
                            // notify to all subscribers that the unread conversations list changed
                            emitter.onNext(conversation);
                        } catch (Exception e) {
                            // notify to all subscribers that an error occurred
                            if (!emitter.isCancelled())
                                emitter.onError(new ConversationsListException(e));
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Conversation conversation = ConversationUtils.decodeConversationFromSnapshot(mUserId, dataSnapshot);
                            ConversationUtils.deleteConversationFromMemory(mConversations, conversation.getConversationId());
                            ConversationUtils.sortConversationsInMemory(mConversations, timestampComparator);
                            // notify to all subscribers that the unread conversations list changed
                            emitter.onNext(conversation); // return the deleted conversation
                        } catch (Exception e) {
                            // notify to all subscribers that an error occurred
                            if (!emitter.isCancelled())
                                emitter.onError(new ConversationsListException(e));
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // TODO: 26/03/2019  
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO: 26/03/2019
                    }
                });

            }
            emitter.setCancellable(() -> node.removeEventListener(mConversationsListener));
            node.addChildEventListener(mConversationsListener);
        }, BackpressureStrategy.DROP);
    }

    private void setConversationRead(final String recipientId) {
        Conversation conversation = ConversationUtils.getConversationById(mConversations, recipientId);
        // check if the conversation is new
        // if it is new set the conversation as read (false), do nothing otherwise
        if (conversation != null && conversation.isNew()) {
            node.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // check if the conversation exists to prevent conversation with only "is_new" value
                    if (snapshot.hasChild(recipientId)) {
                        // update the state
                        node.child(getContext().getString(R.string.firebase_node_new_conversation, recipientId))
                                .setValue(false); // the conversation has been read
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "setConversationRead: cannot mark the conversation as read: " + databaseError.getMessage());
//                    Crashlytics.logException(databaseError.toException());
                }
            });
        }
    }

    @Override
    public void dispose() {
        mUserId = null;
        mConversations = null;
        timestampComparator = null;
        mConversationsListener = null;
        node = null;


        super.dispose();
    }
}
