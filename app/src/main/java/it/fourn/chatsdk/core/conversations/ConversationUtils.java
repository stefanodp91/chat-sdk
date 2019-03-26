package it.fourn.chatsdk.core.conversations;

import com.google.firebase.database.DataSnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.fourn.chatsdk.core.exceptions.FieldNotFoundException;
import it.fourn.chatsdk.core.models.Conversation;
import it.fourn.chatsdk.core.models.ConversationDTO;

class ConversationUtils {
    static Conversation decodeConversationFromSnapshot(String userId, DataSnapshot dataSnapshot) {
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
        if (conversation.getRecipient().equals(userId)) {
            conversation.setConversWith(conversation.getSender());
            conversation.setConversWithFullname(conversation.getSenderFullname());
        } else {
            conversation.setConversWith(conversation.getRecipient());
            conversation.setConversWithFullname(conversation.getRecipientFullName());
        }

        return conversation;
    }

    /**
     * It looks for the conversation with {@code conversationId}
     *
     * @param conversationId the group id to looking for
     * @return the conversation if exists, null otherwise
     */
    static Conversation getConversationById(List<Conversation> conversationList, String conversationId) {
        for (Conversation conversation : conversationList) {
            if (conversation.getConversationId().equals(conversationId)) {
                return conversation;
            }
        }
        return null;
    }

    /**
     * it checks if the conversation already exists.
     * if the conversation exists update it, add it otherwise
     */
    static void saveOrUpdateConversationInMemory(List<Conversation> conversationList, Conversation newConversation) {

        // look for the conversation
        int index = -1;
        for (Conversation tempConversation : conversationList) {
            if (tempConversation.equals(newConversation)) {
                index = conversationList.indexOf(tempConversation);
                break;
            }
        }

        if (index != -1) {
            // conversation already exists
            conversationList.set(index, newConversation); // update the existing conversation
        } else {
            // conversation not exists
            conversationList.add(newConversation); // insert a new conversation
        }
    }

    /**
     * check if the list has al least 1 item.
     * 1 item is already sorted
     */
    static void sortConversationsInMemory(List<Conversation> conversations, Comparator<Conversation> conversationComparator) {
        if (conversations.size() > 1) {
            Collections.sort(conversations, conversationComparator);
        }
    }

    /**
     * it checks if the conversation already exists through its conversationId.
     * if the conversation exists delete it
     */
    static boolean deleteConversationFromMemory(List<Conversation> conversationList, String conversationId) {
        int index = -1;
        for (Conversation tempConversation : conversationList) {
            if (tempConversation.getConversationId().equals(conversationId)) {
                index = conversationList.indexOf(tempConversation);
                break;
            }
        }

        if (index != -1) {
            conversationList.remove(index);
            return true;
        }

        return false;
    }
}
