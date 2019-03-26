package it.fourn.chatsdk.core.models;

import com.google.firebase.database.Exclude;

import java.util.Map;

public interface Conversation {

    @Exclude
    String getConversationId();

    @Exclude
    void setConversationId(String conversationId);

    @Exclude
    String getConversWith();

    @Exclude
    void setConversWith(String conversWith);

    @Exclude
    String getConversWithFullname();

    @Exclude
    void setConversWithFullname(String conversWithFullname);

    @Exclude
    Long getTimestampLong();

    Boolean isNew();

    void setIsNew(Boolean isNew);

    String getLastMessageText();

    void setLastMessageText(String lastMessageText);

    String getRecipient();

    void setRecipient(String recipient);

    String getRecipientFullName();

    void setRecipientFullName(String recipientFullname);

    String getSender();

    void setSender(String sender);

    String getSenderFullname();

    void setSenderFullname(String senderFullname);

    int getStatus();

    void setStatus(int status);

    java.util.Map<String, String> getTimestamp();

    void setTimestamp(Long timestamp);

    Map<String, Object> getExtras();

    void setExtras(Map<String, Object> extras);

    String getChannelType();

    void setChannelType(String channelType);
}
