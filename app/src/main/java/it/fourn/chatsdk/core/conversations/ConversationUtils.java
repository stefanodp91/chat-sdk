package it.fourn.chatsdk.core.conversations;

import com.google.firebase.database.DataSnapshot;

import java.util.Map;

import it.fourn.chatsdk.core.exceptions.FieldNotFoundException;
import it.fourn.chatsdk.core.models.Conversation;
import it.fourn.chatsdk.core.models.ConversationDTO;

class Utils {
    private Conversation decodeConversationFromSnapshot(DataSnapshot dataSnapshot) {
        Conversation conversation = new ConversationDTO();

        // conversationId
        conversation.setConversationId(dataSnapshot.getKey());

        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

        if (map == null) {
            throw new FieldNotFoundException("conversation map cannot be null");
        }

        // is_new
        boolean is_new = (boolean) map.get("is_new");
        conversation.setIsNew(is_new);


        // last_message_text
        String last_message_text = (String) map.get("last_message_text");
        conversation.setLastMessageText(last_message_text);

        // recipient
        String recipient = (String) map.get("recipient");
        conversation.setRecipient(recipient);

        // rrecipient_fullname
        String recipientFullName = (String) map.get("recipient_fullname");
        conversation.setRecipientFullName(recipientFullName);

        // sender
        String sender = (String) map.get("sender");
        conversation.setSender(sender);

        // sender_fullname
        String sender_fullname = (String) map.get("sender_fullname");
        conversation.setSenderFullname(sender_fullname);

        // status
        long status = (long) map.get("status");
        conversation.setStatus((int) status);

        // timestamp
        long timestamp = (long) map.get("timestamp");
        conversation.setTimestamp(timestamp);

        // channel type
        String channelType = (String) map.get("channel_type");
        conversation.setChannelType(channelType);

        // convers with
        if (conversation.getRecipient().equals(mUserId)) {
            conversation.setConversWith(conversation.getSender());
            conversation.setConversWithFullname(conversation.getSenderFullname());
        } else {
            conversation.setConversWith(conversation.getRecipient());
            conversation.setConversWithFullname(conversation.getRecipientFullName());
        }

        return conversation;
    }
}
